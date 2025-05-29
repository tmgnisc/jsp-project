package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.UserControllerImplements;
import model.User;
import org.apache.commons.codec.digest.DigestUtils;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserControllerImplements controller;
    private static final String STATIC_SALT = "NepalNavigatorSalt2025"; // Match with other components

    @Override
    public void init() throws ServletException {
        controller = new UserControllerImplements();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get form parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate inputs
        String errorMessage = validateLogin(email, password);
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/user-side/login.jsp").forward(request, response);
            return;
        }

        // Fetch user by email
        User user = controller.getUserByEmail(email);
        if (user == null) {
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/user-side/login.jsp").forward(request, response);
            return;
        }

        // Hash the provided password with the same salt
        String salt = user.getUsername() + STATIC_SALT;
        String saltedPassword = salt + password;
        String hashedPassword = DigestUtils.shaHex(saltedPassword); // Updated to SHA-256

        // Verify the password
        if (!hashedPassword.equals(getHashedPasswordFromDatabase(user.getId()))) {
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/user-side/login.jsp").forward(request, response);
            return;
        }

        // Successful login - Set session attributes
        request.getSession().setAttribute("user", user); // Store full user object
        request.getSession().setAttribute("username", user.getUsername()); // Store username separately
        request.getSession().setAttribute("email", user.getEmail()); // Store email separately
        request.getSession().setAttribute("role", user.getRole()); // Store role in session
        request.getSession().setAttribute("notify", "Login successful! Welcome, " + user.getUsername() + ".");

        // Redirect based on role
        String redirectPage;
        String role = user.getRole().toLowerCase(); // Ensure case-insensitive comparison
        if ("admin".equals(role)) {
            redirectPage = "/admin-side/dashboard.jsp";
        } else if ("tourist".equals(role) || "local".equals(role)) {
            // Redirect to /index to let IndexServlet handle data fetching
            redirectPage = "/index";
        } else {
            // Default fallback
            redirectPage = "/index";
        }
        response.sendRedirect(request.getContextPath() + redirectPage); // Use redirect instead of forward
    }

    private String validateLogin(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required.";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email format.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }
        return null; // No errors
    }

    private String getHashedPasswordFromDatabase(int userId) {
        String sql = "SELECT password FROM users WHERE id = ?";
        try (var conn = utility.DatabaseConnection.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching password from database: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/user-side/login.jsp").forward(request, response);
    }
}