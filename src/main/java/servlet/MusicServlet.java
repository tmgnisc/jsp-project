package servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import controller.MusicControllerImplements;
import controller.CelebrityControllerImplements;
import model.Music;
import model.Celebrity;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

@WebServlet("/music-dashboard")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class MusicServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MusicControllerImplements controller;
    private CelebrityControllerImplements celebrityController;
    private static final String UPLOAD_DIR = "assets/img";
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(Music.class, "music");
        controller = new MusicControllerImplements();
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

        if ("getMusic".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Music> musicList = controller.getMusicById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!musicList.isEmpty()) {
                    Music item = musicList.get(0);
                    String celebrityIdsStr = item.getCelebrityIds() != null ? 
                        String.join(",", item.getCelebrityIds().stream().map(String::valueOf).toArray(String[]::new)) : "";
                    String json = String.format(
                        "{\"id\":%d,\"artistName\":\"%s\",\"genre\":\"%s\",\"formationYear\":%d,\"description\":\"%s\",\"popularSongs\":\"%s\",\"achievements\":\"%s\",\"youtubeChannelUrl\":\"%s\",\"image\":\"%s\",\"celebrityIds\":\"%s\"}",
                        item.getId(),
                        item.getArtistName() != null ? item.getArtistName().replace("\"", "\\\"") : "",
                        item.getGenre() != null ? item.getGenre().replace("\"", "\\\"") : "",
                        item.getFormationYear(),
                        item.getDescription() != null ? item.getDescription().replace("\"", "\\\"") : "",
                        item.getPopularSongs() != null ? item.getPopularSongs().replace("\"", "\\\"") : "",
                        item.getAchievements() != null ? item.getAchievements().replace("\"", "\\\"") : "",
                        item.getYoutubeChannelUrl() != null ? item.getYoutubeChannelUrl().replace("\"", "\\\"") : "",
                        item.getImage() != null ? item.getImage().replace("\"", "\\\"") : "",
                        celebrityIdsStr
                    );
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Music not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid music ID\"}");
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Music> musicList = controller.getMusicById(id);
                System.out.println("Retrieved " + (musicList != null ? musicList.size() : 0) + " items for edit with ID: " + id);
                if (!musicList.isEmpty()) {
                    request.setAttribute("musicToEdit", musicList.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No music found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid music ID.");
                System.err.println("NumberFormatException: " + e.getMessage());
            }
        }

        List<Music> musicList = controller.getAllData();
        List<Celebrity> celebrityList = celebrityController.getAllData(); // Fetch all celebrities for the form
        request.setAttribute("musicList", musicList);
        request.setAttribute("celebrityList", celebrityList);
        request.getRequestDispatcher("/admin-side/music.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String artistName = request.getParameter("artistName");
        String genre = request.getParameter("genre");
        String formationYearStr = request.getParameter("formationYear");
        String description = request.getParameter("description");
        String popularSongs = request.getParameter("popularSongs");
        String achievements = request.getParameter("achievements");
        String youtubeChannelUrl = request.getParameter("youtubeChannelUrl");
        String[] celebrityIdsArray = request.getParameterValues("celebrityIds"); // Get selected celebrity IDs

        int formationYear;
        try {
            formationYear = Integer.parseInt(formationYearStr);
        } catch (NumberFormatException e) {
            formationYear = 0;
        }

        // Convert celebrity IDs to List<Integer>
        List<Integer> celebrityIds = new ArrayList<>();
        if (celebrityIdsArray != null) {
            for (String celebrityId : celebrityIdsArray) {
                try {
                    celebrityIds.add(Integer.parseInt(celebrityId));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid celebrity ID: " + celebrityId);
                }
            }
        }

        String imagePath = null;
        if ("add".equals(action) || "edit".equals(action)) {
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
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
                }
            }
        }

        String notifyMessage = "";
        if ("delete".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = controller.deleteMusic(id);
                notifyMessage = success ? "Music deleted successfully!" : "Failed to delete music.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid music ID.";
            }
        } else if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Music> existingItems = controller.getMusicById(id);
                String finalImagePath = (imagePath != null) ? imagePath : (existingItems.isEmpty() ? null : existingItems.get(0).getImage());
                Music music = new Music(id, artistName, genre, formationYear, description,
                                        popularSongs, achievements, youtubeChannelUrl, finalImagePath, celebrityIds);
                boolean success = controller.editMusic(music);
                notifyMessage = success ? "Music updated successfully!" : "Failed to update music.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid music ID.";
            }
        } else if ("add".equals(action)) {
            Music music = new Music(0, artistName, genre, formationYear, description,
                                    popularSongs, achievements, youtubeChannelUrl, imagePath, celebrityIds);
            boolean success = controller.addMusic(music);
            notifyMessage = success ? "Music added successfully!" : "Failed to add music.";
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        List<Music> musicList = controller.getAllData();
        List<Celebrity> celebrityList = celebrityController.getAllData();
        request.setAttribute("musicList", musicList);
        request.setAttribute("celebrityList", celebrityList);
        request.getRequestDispatcher("/admin-side/music.jsp").forward(request, response);
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
        return "";
    }
}