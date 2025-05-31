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
import controller.CelebrityControllerImplements;
import model.Celebrity;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

@WebServlet("/celebrity-dashboard")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class CelebrityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CelebrityControllerImplements controller;
    private static final String UPLOAD_DIR = "assets/img";
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(Celebrity.class, "celebrities"); // Ensure table exists
        controller = new CelebrityControllerImplements();
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

        if ("getCelebrity".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Celebrity> celebrityList = controller.getCelebrityById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!celebrityList.isEmpty()) {
                    Celebrity item = celebrityList.get(0);
                    String json = String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"bio\":\"%s\",\"image\":\"%s\"}",
                        item.getId(),
                        item.getName() != null ? item.getName().replace("\"", "\\\"") : "",
                        item.getBio() != null ? item.getBio().replace("\"", "\\\"") : "",
                        item.getImage() != null ? item.getImage().replace("\"", "\\\"") : ""
                    );
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Celebrity not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid celebrity ID\"}");
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<Celebrity> celebrityList = controller.getCelebrityById(id);
                System.out.println("Retrieved " + (celebrityList != null ? celebrityList.size() : 0) + " items for edit with ID: " + id);
                if (!celebrityList.isEmpty()) {
                    request.setAttribute("celebrityToEdit", celebrityList.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No celebrity found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid celebrity ID.");
                System.err.println("NumberFormatException: " + e.getMessage());
            }
        }

        List<Celebrity> celebrityList = controller.getAllData();
        request.setAttribute("celebrityList", celebrityList);
        request.getRequestDispatcher("/admin-side/celebrity.jsp").forward(request, response);
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
                boolean success = controller.deleteCelebrity(id);
                notifyMessage = success ? "Celebrity deleted successfully!" : "Failed to delete celebrity.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid celebrity ID.";
            }
        } else if ("add".equals(action) || "edit".equals(action)) {
            String name = request.getParameter("name");
            String bio = request.getParameter("bio");

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
                    List<Celebrity> existingItems = controller.getCelebrityById(id);
                    if (existingItems.isEmpty()) {
                        notifyMessage = "Celebrity not found with ID: " + id;
                    } else {
                        String finalImagePath = (imagePath != null) ? imagePath : existingItems.get(0).getImage();
                        Celebrity celebrity = new Celebrity(id, name, bio, finalImagePath);
                        boolean success = controller.editCelebrity(celebrity);
                        notifyMessage = success ? "Celebrity updated successfully!" : "Failed to update celebrity.";
                    }
                } catch (NumberFormatException e) {
                    notifyMessage = "Invalid celebrity ID.";
                    System.err.println("NumberFormatException in edit: " + e.getMessage());
                }
            } else if ("add".equals(action)) {
                Celebrity celebrity = new Celebrity(0, name, bio, imagePath);
                boolean success = controller.addCelebrity(celebrity);
                notifyMessage = success ? "Celebrity added successfully!" : "Failed to add celebrity.";
            }
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        List<Celebrity> celebrityList = controller.getAllData();
        request.setAttribute("celebrityList", celebrityList);
        request.getRequestDispatcher("/admin-side/celebrity.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
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
}