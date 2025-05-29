package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Attraction;
import model.AttractionComment;
import model.User;
import controller.AttractionCommentController;
import controller.AttractionControllerImplements;
import utility.DatabaseConnection;

/**
 * Servlet implementation class UserAttractionServlet
 */
@WebServlet({"/attractions", "/attraction-detail"})
public class UserAttractionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AttractionControllerImplements attractionController;
    private AttractionCommentController commentController;

    @Override
    public void init() throws ServletException {
        attractionController = new AttractionControllerImplements();
        commentController = new AttractionCommentController();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/attractions".equals(path)) {
            String searchQuery = request.getParameter("search");
            String location = request.getParameter("location");
            String category = request.getParameter("category");

            List<Attraction> attractions = attractionController.getAllData();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                attractions = attractions.stream()
                        .filter(attraction -> attraction.getName() != null && attraction.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            String selectedLocation = (location != null && !location.trim().isEmpty()) ? location.toLowerCase() : "all";
            if (!"all".equals(selectedLocation)) {
                attractions = attractions.stream()
                        .filter(attraction -> attraction.getLocation() != null && attraction.getLocation().toLowerCase().contains(selectedLocation))
                        .collect(Collectors.toList());
            }

            String selectedCategory = (category != null && !category.trim().isEmpty()) ? category.toLowerCase() : "all";
            if (!"all".equals(selectedCategory)) {
                attractions = attractions.stream()
                        .filter(attraction -> attraction.getCategory() != null && attraction.getCategory().toLowerCase().equals(selectedCategory))
                        .collect(Collectors.toList());
            }

            request.setAttribute("attractions", attractions);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedLocation", selectedLocation);
            request.setAttribute("selectedCategory", selectedCategory);
            request.getRequestDispatcher("/user-side/attraction.jsp").forward(request, response);
        } else if ("/attraction-detail".equals(path)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int id = Integer.parseInt(idParam);
                    List<Attraction> attractions = attractionController.getAttractionById(id);
                    if (!attractions.isEmpty()) {
                        Attraction attraction = attractions.get(0);
                        List<AttractionComment> comments = commentController.getCommentsByAttractionId(id);
                        request.setAttribute("attraction", attraction);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.getRequestDispatcher("/user-side/attraction-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/attractions?error=attraction_not_found");
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/attractions?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/attractions?error=invalid_id");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/attraction-detail".equals(path)) {
            String idParam = request.getParameter("attractionId"); // Matches the form in attraction-detail.jsp
            String commentText = request.getParameter("commentText");

            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                request.getSession().setAttribute("redirectAfterLogin", request.getRequestURI() + "?id=" + idParam);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (idParam != null && commentText != null && !commentText.trim().isEmpty()) {
                try {
                    int attractionId = Integer.parseInt(idParam);
                    boolean success = commentController.addComment(
                        attractionId,
                        user.getId(),
                        user.getUsername(),
                        commentText.trim()
                    );
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/attraction-detail?id=" + attractionId);
                    } else {
                        List<Attraction> attractions = attractionController.getAttractionById(attractionId);
                        Attraction attraction = attractions.isEmpty() ? null : attractions.get(0);
                        List<AttractionComment> comments = commentController.getCommentsByAttractionId(attractionId);
                        request.setAttribute("attraction", attraction);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/attraction-detail.jsp").forward(request, response);
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/attractions?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/attraction-detail?id=" + idParam + "&error=invalid_input");
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

    private int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching userId for username " + username + ": " + e.getMessage());
        }
        return -1; // Indicates failure to find user
    }
}