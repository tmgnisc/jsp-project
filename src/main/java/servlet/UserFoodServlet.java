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
            String searchQuery = request.getParameter("search");
            String region = request.getParameter("region");
            String tag = request.getParameter("tag");

            List<FoodItem> foodItems = foodController.getAllData();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                foodItems = foodItems.stream()
                        .filter(food -> food.getName() != null && food.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            String selectedRegion = (region != null && !region.trim().isEmpty()) ? region.toLowerCase() : "all";
            if (!"all".equals(selectedRegion)) {
                foodItems = foodItems.stream()
                        .filter(food -> food.getRegion() != null && food.getRegion().toLowerCase().contains(selectedRegion))
                        .collect(Collectors.toList());
            }

            String selectedTag = (tag != null && !tag.trim().isEmpty()) ? tag.toLowerCase() : "all";
            if (!"all".equals(selectedTag)) {
                foodItems = foodItems.stream()
                        .filter(food -> food.getTag() != null && food.getTag().toLowerCase().equals(selectedTag))
                        .collect(Collectors.toList());
            }

            request.setAttribute("foodItems", foodItems);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedRegion", selectedRegion);
            request.setAttribute("selectedTag", selectedTag);
            request.getRequestDispatcher("/user-side/food.jsp").forward(request, response);
        } else if ("/food-detail".equals(path)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int id = Integer.parseInt(idParam);
                    List<FoodItem> foodItems = foodController.getFoodItemById(id);
                    if (!foodItems.isEmpty()) {
                        FoodItem food = foodItems.get(0);
                        List<Comment> comments = commentController.getCommentsByFoodId(id);
                        request.setAttribute("food", food);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.getRequestDispatcher("/user-side/food-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/foods?error=food_not_found");
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/foods?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/foods?error=invalid_id");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/food-detail".equals(path)) {
            String idParam = request.getParameter("foodId"); // Changed from "id" to "foodId" to match the form
            String commentText = request.getParameter("commentText");

            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
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
                        commentText.trim()
                    );
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/food-detail?id=" + foodId);
                    } else {
                        FoodItem food = foodController.getFoodItemById(foodId).get(0);
                        List<Comment> comments = commentController.getCommentsByFoodId(foodId);
                        request.setAttribute("food", food);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/food-detail.jsp").forward(request, response);
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/foods?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/food-detail?id=" + idParam + "&error=invalid_input");
            }
        } else {
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
        super.destroy();
    }
}