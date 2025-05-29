package servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.CommentController;
import controller.FoodItemControllerImplements;
import model.Comment;
import model.FoodItem;
import model.User;

/**
 * Servlet implementation class UserFoodServlet
 */
@WebServlet({"/foods", "/food-detail"})
public class UserFoodServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FoodItemControllerImplements foodController;
    private CommentController commentController;

    @Override
    public void init() throws ServletException {
        foodController = new FoodItemControllerImplements();
        commentController = new CommentController();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/foods".equals(path)) {
            // Get search parameters
            String searchQuery = request.getParameter("search");
            String region = request.getParameter("region");
            String tag = request.getParameter("tag");

            // Fetch all food items
            List<FoodItem> foodItems = foodController.getAllData();

            // Filter based on search query
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                foodItems = foodItems.stream()
                        .filter(food -> food.getName() != null && food.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            // Filter based on region
            String selectedRegion = (region != null && !region.trim().isEmpty()) ? region.toLowerCase() : "all";
            if (!"all".equals(selectedRegion)) {
                foodItems = foodItems.stream()
                        .filter(food -> food.getRegion() != null && food.getRegion().toLowerCase().contains(selectedRegion))
                        .collect(Collectors.toList());
            }

            // Filter based on tag
            String selectedTag = (tag != null && !tag.trim().isEmpty()) ? tag.toLowerCase() : "all";
            if (!"all".equals(selectedTag)) {
                foodItems = foodItems.stream()
                        .filter(food -> food.getTag() != null && food.getTag().toLowerCase().equals(selectedTag))
                        .collect(Collectors.toList());
            }

            // Set attributes for JSP
            request.setAttribute("foodItems", foodItems);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedRegion", selectedRegion);
            request.setAttribute("selectedTag", selectedTag);
            request.getRequestDispatcher("/user-side/food.jsp").forward(request, response);
        } else if ("/food-detail".equals(path)) {
            // Existing food-detail logic remains unchanged
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int id = Integer.parseInt(idParam);
                    FoodItem food = foodController.getFoodItemById(id).get(0);
                    if (food != null) {
                        List<Comment> comments = commentController.getCommentsByFoodId(id);
                        request.setAttribute("food", food);
                        request.setAttribute("comments", comments);
                        request.getRequestDispatcher("/user-side/food-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/foods");
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/foods");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/foods");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/food-detail".equals(path)) {
            // Handle comment posting
            String idParam = request.getParameter("id");
            String commentText = request.getParameter("commentText");

            // Check if user is logged in
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                // Redirect to login if not logged in
                request.getSession().setAttribute("redirectAfterLogin", request.getRequestURI() + "?id=" + idParam);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (idParam != null && commentText != null && !commentText.trim().isEmpty()) {
                try {
                    int foodId = Integer.parseInt(idParam);
                    boolean success = commentController.addComment(
                        foodId,
                        user.getId(),
                        user.getUsername(),
                        commentText
                    );
                    if (success) {
                        // Redirect to refresh the page with the new comment
                        response.sendRedirect(request.getContextPath() + "/food-detail?id=" + foodId);
                    } else {
                        // Handle error (e.g., display error message)
                        FoodItem food = foodController.getFoodItemById(foodId).get(0); // Adjust based on implementation
                        List<Comment> comments = commentController.getCommentsByFoodId(foodId);
                        request.setAttribute("food", food);
                        request.setAttribute("comments", comments);
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/food-detail.jsp").forward(request, response);
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/foods");
                }
            } else {
                // Invalid input, redirect back to the same page
                response.sendRedirect(request.getContextPath() + "/food-detail?id=" + idParam);
            }
        } else {
            doGet(request, response);
        }
    }
}