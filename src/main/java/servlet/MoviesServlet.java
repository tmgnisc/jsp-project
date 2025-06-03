package servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import controller.CelebrityControllerImplements;
import controller.MovieControllerImplements;
import model.Celebrity;
import model.Movie;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

@WebServlet("/movie-dashboard")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MovieControllerImplements movieController;
    private CelebrityControllerImplements celebrityController;
    private static final String UPLOAD_DIR = "assets/img";
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(Movie.class, "movies");
        movieController = new MovieControllerImplements();
        celebrityController = new CelebrityControllerImplements();
        uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        System.out.println("Upload path: " + uploadPath);
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("Upload directory created: " + created);
            if (!created) {
                System.err.println("Failed to create upload directory: " + uploadPath);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        request.setAttribute("activeUser", "Admin");
        request.setAttribute("notify", request.getSession().getAttribute("notify") != null ? request.getSession().getAttribute("notify") : "");
        request.getSession().removeAttribute("notify");

        if ("getMovie".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Movie> movieList = movieController.getMovieById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!movieList.isEmpty()) {
                    Movie item = movieList.get(0);
                    StringBuilder celebIdsJson = new StringBuilder("[");
                    List<Integer> celebIds = item.getCelebrityIds();
                    for (int i = 0; i < celebIds.size(); i++) {
                        celebIdsJson.append(celebIds.get(i));
                        if (i < celebIds.size() - 1) celebIdsJson.append(",");
                    }
                    celebIdsJson.append("]");
                    // Use escapeJson to handle special characters
                    String json = String.format(
                        "{\"id\":%d,\"title\":\"%s\",\"description\":\"%s\",\"genre\":\"%s\",\"rating\":%f,\"trailerUrl\":\"%s\",\"ticketBookingUrl\":\"%s\",\"image\":\"%s\",\"celebrityIds\":%s}",
                        item.getId(),
                        escapeJson(item.getTitle()),
                        escapeJson(item.getDescription()),
                        escapeJson(item.getGenre()),
                        item.getRating(),
                        escapeJson(item.getTrailerUrl()),
                        escapeJson(item.getTicketBookingUrl()),
                        escapeJson(item.getImage()),
                        celebIdsJson.toString()
                    );
                    System.out.println("Sending JSON response: " + json); // Debug log
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Movie not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid movie ID\"}");
            } catch (IOException e) {
                System.err.println("Error writing JSON response: " + e.getMessage());
                throw e; // Re-throw to ensure the error is logged properly
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Movie> movieList = movieController.getMovieById(id);
                System.out.println("Retrieved " + (movieList != null ? movieList.size() : 0) + " items for edit with ID: " + id);
                if (!movieList.isEmpty()) {
                    request.setAttribute("movieToEdit", movieList.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No movie found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid movie ID.");
                System.err.println("NumberFormatException: " + e.getMessage());
            }
        }

        List<Celebrity> celebrityList = celebrityController.getAllData();
        request.setAttribute("celebrityList", celebrityList);

        List<Movie> movieList = movieController.getAllData();
        request.setAttribute("movieList", movieList);
        request.getRequestDispatcher("/admin-side/movies.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        String notifyMessage = "";
        if ("delete".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = movieController.deleteMovie(id);
                notifyMessage = success ? "Movie deleted successfully!" : "Failed to delete movie.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid movie ID.";
            }
        } else if ("add".equals(action) || "edit".equals(action)) {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String genre = request.getParameter("genre");
            String ratingStr = request.getParameter("rating");
            String trailerUrl = request.getParameter("trailerUrl");
            String ticketBookingUrl = request.getParameter("ticketBookingUrl");
            String[] celebrityIdsArray = request.getParameterValues("celebrityIds");

            float rating;
            try {
                rating = ratingStr != null ? Float.parseFloat(ratingStr) : 0.0f;
            } catch (NumberFormatException e) {
                rating = 0.0f;
                System.err.println("Invalid rating format: " + ratingStr);
            }

            List<Integer> celebrityIds = new ArrayList<>();
            if (celebrityIdsArray != null) {
                for (String id : celebrityIdsArray) {
                    if (!id.trim().isEmpty()) {
                        celebrityIds.add(Integer.parseInt(id.trim()));
                    }
                }
            }

            String imagePath = null;
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                if (fileName != null && !fileName.isEmpty()) {
                    String absoluteFilePath = uploadPath + File.separator + fileName;
                    System.out.println("Attempting to save file to: " + absoluteFilePath);
                    try {
                        filePart.write(absoluteFilePath);
                        System.out.println("File saved successfully to: " + absoluteFilePath);
                        imagePath = "/" + UPLOAD_DIR + "/" + fileName;
                        System.out.println("Image path stored: " + imagePath);
                    } catch (IOException e) {
                        System.err.println("Error saving file: " + e.getMessage());
                        e.printStackTrace();
                        notifyMessage = "Error saving image: " + e.getMessage();
                    }
                } else {
                    System.err.println("No valid filename extracted for image upload.");
                }
            }

            if ("edit".equals(action) && idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    List<Movie> existingItems = movieController.getMovieById(id);
                    if (existingItems.isEmpty()) {
                        notifyMessage = "Movie not found with ID: " + id;
                    } else {
                        String finalImagePath = (imagePath != null) ? imagePath : existingItems.get(0).getImage();
                        Movie movie = new Movie(id, title, description, genre, rating, trailerUrl, ticketBookingUrl, finalImagePath, celebrityIds);
                        boolean success = movieController.editMovie(movie);
                        notifyMessage = success ? "Movie updated successfully!" : "Failed to update movie.";
                    }
                } catch (NumberFormatException e) {
                    notifyMessage = "Invalid movie ID.";
                    System.err.println("NumberFormatException in edit: " + e.getMessage());
                }
            } else if ("add".equals(action)) {
                Movie movie = new Movie(0, title, description, genre, rating, trailerUrl, ticketBookingUrl, imagePath, celebrityIds);
                boolean success = movieController.addMovie(movie);
                notifyMessage = success ? "Movie added successfully!" : "Failed to add movie.";
            }
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        List<Celebrity> celebrityList = celebrityController.getAllData();
        request.setAttribute("celebrityList", celebrityList);

        List<Movie> movieList = movieController.getAllData();
        request.setAttribute("movieList", movieList);
        request.getRequestDispatcher("/admin-side/movies.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection();
        super.destroy();
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("Content-Disposition: " + contentDisp);
        if (contentDisp != null) {
            String[] items = contentDisp.split(";");
            for (String s : items) {
                if (s.trim().startsWith("filename")) {
                    String fileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                    System.out.println("Extracted file name: " + fileName);
                    return fileName;
                }
            }
        }
        System.err.println("No filename found in content-disposition.");
        return null;
    }

    // Helper method to escape strings for JSON
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\") // Escape backslashes
                    .replace("\"", "\\\"") // Escape double quotes
                    .replace("\n", "\\n")  // Escape newlines
                    .replace("\r", "\\r")  // Escape carriage returns
                    .replace("\t", "\\t"); // Escape tabs
    }
}