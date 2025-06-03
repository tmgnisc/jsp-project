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

@WebServlet("/food-dashboard")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class FoodServlet extends HttpServlet {
    private FoodItemControllerImplements controller;
    private static final String UPLOAD_DIR = "assets/img";
    private String uploadPath;

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(FoodItem.class, "food_items");
        controller = new FoodItemControllerImplements();
        uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
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

        if ("getFoodItem".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<FoodItem> foodItems = controller.getFoodItemById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!foodItems.isEmpty()) {
                    FoodItem item = foodItems.get(0);
                    // Use escapeJson to handle special characters in all string fields
                    String json = String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"description\":\"%s\",\"category\":\"%s\",\"image\":\"%s\"," +
                        "\"ingredients\":\"%s\",\"preparationMethod\":\"%s\",\"servingSuggestions\":\"%s\"," +
                        "\"culturalSignificance\":\"%s\",\"region\":\"%s\",\"tag\":\"%s\"}",
                        item.getId(),
                        escapeJson(item.getName()),
                        escapeJson(item.getDescription()),
                        escapeJson(item.getCategory()),
                        escapeJson(item.getImage()),
                        escapeJson(item.getIngredients()),
                        escapeJson(item.getPreparationMethod()),
                        escapeJson(item.getServingSuggestions()),
                        escapeJson(item.getCulturalSignificance()),
                        escapeJson(item.getRegion()),
                        escapeJson(item.getTag())
                    );
                    System.out.println("Sending JSON response: " + json); // Debug log
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Food item not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid food item ID\"}");
            } catch (IOException e) {
                System.err.println("Error writing JSON response: " + e.getMessage());
                throw e; // Re-throw to ensure the error is logged properly
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<FoodItem> foodItems = controller.getFoodItemById(id);
                if (!foodItems.isEmpty()) {
                    request.setAttribute("foodItemToEdit", foodItems.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No food item found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid food item ID.");
            }
        }

        List<FoodItem> foodItems = controller.getAllData();
        request.setAttribute("foodItems", foodItems);
        request.getRequestDispatcher("/admin-side/foods.jsp").forward(request, response);
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
        String region = request.getParameter("region");
        String tag = request.getParameter("tag");

        String imagePath = null;
        if ("add".equals(action) || "edit".equals(action)) {
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                // Avoid overwriting existing files by adding a timestamp
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                String absoluteFilePath = uploadPath + File.separator + uniqueFileName;
                filePart.write(absoluteFilePath);
                imagePath = "/" + UPLOAD_DIR + "/" + uniqueFileName;
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
                if (existingItems.isEmpty()) {
                    notifyMessage = "Food item not found.";
                } else {
                    String finalImagePath = (imagePath != null) ? imagePath : existingItems.get(0).getImage();
                    FoodItem foodItem = new FoodItem(id, name, description, category, finalImagePath, 
                                                    ingredients, preparationMethod, servingSuggestions, 
                                                    culturalSignificance, region, tag);
                    boolean success = controller.editFoodItem(foodItem);
                    notifyMessage = success ? "Food item updated successfully!" : "Failed to update food item.";
                }
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid food item ID.";
            }
        } else if ("add".equals(action)) {
            FoodItem foodItem = new FoodItem(0, name, description, category, imagePath, 
                                             ingredients, preparationMethod, servingSuggestions, 
                                             culturalSignificance, region, tag);
            boolean success = controller.addFoodItem(foodItem);
            notifyMessage = success ? "Food item added successfully!" : "Failed to add food item.";
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        response.sendRedirect(request.getContextPath() + "/food-dashboard");
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection();
        super.destroy();
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            String[] items = contentDisp.split(";");
            for (String s : items) {
                if (s.trim().startsWith("filename")) {
                    return s.substring(s.indexOf("=") + 2, s.length() - 1);
                }
            }
        }
        return "";
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