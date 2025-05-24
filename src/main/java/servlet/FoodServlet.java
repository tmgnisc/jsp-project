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
import controller.FoodItemControllerImplements;
import model.FoodItem;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

@SuppressWarnings("serial")
@WebServlet("/food")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class FoodServlet extends HttpServlet {
    private FoodItemControllerImplements controller;
    private static final String UPLOAD_DIR = "assets/img"; // Relative to web app root
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(FoodItem.class, "food_items"); // Ensure table exists
        controller = new FoodItemControllerImplements();
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

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<FoodItem> foodItems = controller.getFoodItemById(id);
                System.out.println("Retrieved " + (foodItems != null ? foodItems.size() : 0) + " items for edit with ID: " + id);
                if (!foodItems.isEmpty()) {
                    request.setAttribute("foodItemToEdit", foodItems.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No food item found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid food item ID.");
                System.err.println("NumberFormatException: " + e.getMessage());
            }
        }

        List<FoodItem> foodItems = controller.getAllData();
        request.setAttribute("foodItems", foodItems);
        request.getRequestDispatcher("/admin-side/foodItem.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String ingredients = request.getParameter("ingredients");
        String preparationMethod = request.getParameter("preparationMethod");
        String servingSuggestions = request.getParameter("servingSuggestions");
        String culturalSignificance = request.getParameter("culturalSignificance");

        String imagePath = null;
        Part filePart = request.getPart("image");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = extractFileName(filePart);
            String absoluteFilePath = uploadPath + File.separator + fileName;
            System.out.println("Attempting to save file to: " + absoluteFilePath);
            try {
                filePart.write(absoluteFilePath);
                System.out.println("File saved successfully to: " + absoluteFilePath);
                imagePath = "/" + UPLOAD_DIR + "/" + fileName; // Relative URL: /assets/img/bangkok.png
                System.out.println("Image path stored: " + imagePath);
            } catch (IOException e) {
                System.err.println("Error saving file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        String notifyMessage = "";
        if ("delete".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = controller.deleteFoodItem(id);
                notifyMessage = success ? "Food item deleted successfully!" : "Failed to delete food item.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid food item ID.";
            }
        } else if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<FoodItem> existingItems = controller.getFoodItemById(id);
                String finalImagePath = (imagePath != null) ? imagePath : (existingItems.isEmpty() ? null : existingItems.get(0).getImage());
                FoodItem foodItem = new FoodItem(id, name, description, category, finalImagePath, 
                                                ingredients, preparationMethod, servingSuggestions, 
                                                culturalSignificance);
                boolean success = controller.editFoodItem(foodItem);
                notifyMessage = success ? "Food item updated successfully!" : "Failed to update food item.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid food item ID.";
            }
        } else {
            FoodItem foodItem = new FoodItem(0, name, description, category, imagePath, 
                                             ingredients, preparationMethod, servingSuggestions, 
                                             culturalSignificance);
            boolean success = controller.addFoodItem(foodItem);
            notifyMessage = success ? "Food item added successfully!" : "Failed to add food item.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        response.sendRedirect(request.getContextPath() + "/food");
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