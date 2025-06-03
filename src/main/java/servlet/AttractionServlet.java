package servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import controller.AttractionControllerImplements;
import model.Attraction;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

@WebServlet("/attraction-dashboard")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class AttractionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AttractionControllerImplements controller;
    private static final String UPLOAD_DIR = "assets/img";
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(Attraction.class, "attractions");
        // Fixed the syntax error: Removed "Então" and correctly assigned the controller
        controller = new AttractionControllerImplements();
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

        if ("getAttraction".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Attraction> attractions = controller.getAttractionById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!attractions.isEmpty()) {
                    Attraction item = attractions.get(0);
                    String json = String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"location\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"image\":\"%s\",\"bestTimeToVisit\":\"%s\",\"howToReach\":\"%s\",\"entryFee\":\"%s\",\"openingHours\":\"%s\",\"nearbyAttractions\":\"%s\"}",
                        item.getId(),
                        escapeJson(item.getName()),
                        escapeJson(item.getLocation()),
                        escapeJson(item.getDescription()),
                        escapeJson(item.getCategory()),
                        escapeJson(item.getImage()),
                        escapeJson(item.getBestTimeToVisit()),
                        escapeJson(item.getHowToReach()),
                        escapeJson(item.getEntryFee()),
                        escapeJson(item.getOpeningHours()),
                        escapeJson(item.getNearbyAttractions())
                    );
                    System.out.println("Sending JSON response: " + json);
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Attraction not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid attraction ID\"}");
            } catch (IOException e) {
                System.err.println("Error writing JSON response: " + e.getMessage());
                throw e;
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Attraction> attractions = controller.getAttractionById(id);
                System.out.println("Retrieved " + (attractions != null ? attractions.size() : 0) + " items for edit with ID: " + id);
                if (!attractions.isEmpty()) {
                    request.setAttribute("attractionToEdit", attractions.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No attraction found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid attraction ID.");
                System.err.println("NumberFormatException: " + e.getMessage());
            }
        }

        List<Attraction> attractions = controller.getAllData();
        request.setAttribute("attractions", attractions);
        request.getRequestDispatcher("/admin-side/attractions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String location = request.getParameter("location");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String bestTimeToVisit = request.getParameter("bestTimeToVisit");
        String howToReach = request.getParameter("howToReach");
        String entryFee = request.getParameter("entryFee");
        String openingHours = request.getParameter("openingHours");
        String nearbyAttractions = request.getParameter("nearbyAttractions");

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
                boolean success = controller.deleteAttraction(id);
                notifyMessage = success ? "Attraction deleted successfully!" : "Failed to delete attraction.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid attraction ID.";
            }
        } else if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Attraction> existingItems = controller.getAttractionById(id);
                String finalImagePath = (imagePath != null) ? imagePath : (existingItems.isEmpty() ? null : existingItems.get(0).getImage());
                Attraction attraction = new Attraction(id, name, location, description, category, finalImagePath,
                                                      bestTimeToVisit, howToReach, entryFee, openingHours, nearbyAttractions);
                boolean success = controller.editAttraction(attraction);
                notifyMessage = success ? "Attraction updated successfully!" : "Failed to update attraction.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid attraction ID.";
            }
        } else if ("add".equals(action)) {
            Attraction attraction = new Attraction(0, name, location, description, category, imagePath,
                                                  bestTimeToVisit, howToReach, entryFee, openingHours, nearbyAttractions);
            boolean success = controller.addAttraction(attraction);
            notifyMessage = success ? "Attraction added successfully!" : "Failed to add attraction.";
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        List<Attraction> attractions = controller.getAllData();
        request.setAttribute("attractions", attractions);
        request.getRequestDispatcher("/admin-side/attractions.jsp").forward(request, response);
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

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}