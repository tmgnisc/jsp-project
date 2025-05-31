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
import controller.CelebrityControllerImplements;
import controller.SportControllerImplements;
import model.Celebrity;
import model.Sport;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

@WebServlet("/sports-dashboard")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class SportsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SportControllerImplements controller;
    private CelebrityControllerImplements celebrityController;
    private static final String UPLOAD_DIR = "assets/img";
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(Sport.class, "sports");
        controller = new SportControllerImplements();
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

        if ("getSport".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Sport> sportList = controller.getSportById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!sportList.isEmpty()) {
                    Sport item = sportList.get(0);
                    StringBuilder celebIdsJson = new StringBuilder("[");
                    List<Integer> celebIds = item.getCelebrityIds();
                    for (int i = 0; i < celebIds.size(); i++) {
                        celebIdsJson.append(celebIds.get(i));
                        if (i < celebIds.size() - 1) celebIdsJson.append(",");
                    }
                    celebIdsJson.append("]");
                    String json = String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"status\":\"%s\",\"history\":\"%s\",\"rules\":\"%s\",\"image\":\"%s\",\"celebrityIds\":%s}",
                        item.getId(),
                        item.getName() != null ? item.getName().replace("\"", "\\\"") : "",
                        item.getDescription() != null ? item.getDescription().replace("\"", "\\\"") : "",
                        item.getCategory() != null ? item.getCategory().replace("\"", "\\\"") : "",
                        item.getStatus() != null ? item.getStatus().replace("\"", "\\\"") : "",
                        item.getHistory() != null ? item.getHistory().replace("\"", "\\\"") : "",
                        item.getRules() != null ? item.getRules().replace("\"", "\\\"") : "",
                        item.getImage() != null ? item.getImage().replace("\"", "\\\"") : "",
                        celebIdsJson.toString()
                    );
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Sport not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid sport ID\"}");
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Sport> sportList = controller.getSportById(id);
                System.out.println("Retrieved " + (sportList != null ? sportList.size() : 0) + " items for edit with ID: " + id);
                if (!sportList.isEmpty()) {
                    request.setAttribute("sportToEdit", sportList.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No sport found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid sport ID.");
                System.err.println("NumberFormatException: " + e.getMessage());
            }
        }

        List<Celebrity> celebrityList = celebrityController.getAllData();
        request.setAttribute("celebrityList", celebrityList);
        List<Sport> sportList = controller.getAllData();
        request.setAttribute("sportList", sportList);
        request.getRequestDispatcher("/admin-side/sports.jsp").forward(request, response);
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
                boolean success = controller.deleteSport(id);
                notifyMessage = success ? "Sport deleted successfully!" : "Failed to delete sport.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid sport ID.";
            }
        } else if ("add".equals(action) || "edit".equals(action)) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String category = request.getParameter("category");
            String status = request.getParameter("status");
            String history = request.getParameter("history");
            String rules = request.getParameter("rules");
            String[] celebrityIdsArray = request.getParameterValues("celebrityIds");

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
                    List<Sport> existingItems = controller.getSportById(id);
                    if (existingItems.isEmpty()) {
                        notifyMessage = "Sport not found with ID: " + id;
                    } else {
                        String finalImagePath = (imagePath != null) ? imagePath : existingItems.get(0).getImage();
                        Sport sport = new Sport(id, name, description, category, status, history, rules, finalImagePath, celebrityIds);
                        boolean success = controller.editSport(sport);
                        notifyMessage = success ? "Sport updated successfully!" : "Failed to update sport.";
                    }
                } catch (NumberFormatException e) {
                    notifyMessage = "Invalid sport ID.";
                    System.err.println("NumberFormatException in edit: " + e.getMessage());
                }
            } else if ("add".equals(action)) {
                Sport sport = new Sport(0, name, description, category, status, history, rules, imagePath, celebrityIds);
                boolean success = controller.addSport(sport);
                notifyMessage = success ? "Sport added successfully!" : "Failed to add sport.";
            }
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        List<Celebrity> celebrityList = celebrityController.getAllData();
        request.setAttribute("celebrityList", celebrityList);
        List<Sport> sportList = controller.getAllData();
        request.setAttribute("sportList", sportList);
        request.getRequestDispatcher("/admin-side/sports.jsp").forward(request, response);
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