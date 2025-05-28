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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
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
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        String role = request.getParameter("role");

        // Validate inputs
        String errorMessage = validateRegistration(username, email, password, confirmPassword, role);
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/user-side/register.jsp").forward(request, response);
            return;
        }

        // Check for duplicate username or email
        errorMessage = checkForDuplicates(username, email);
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/user-side/register.jsp").forward(request, response);
            return;
        }

        // Hash the password using SHA-256
        String salt = username + STATIC_SALT;
        String saltedPassword = salt + password;
        String hashedPassword = DigestUtils.shaHex(saltedPassword);

        // Create user object with fullName set to username
        User user = new User(0, username, email, username, hashedPassword, role, "active");

        // Add user to database
        boolean success = controller.addUser(user);
        if (success) {
            request.getSession().setAttribute("notify", "Registration successful! Please log in.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/user-side/register.jsp").forward(request, response);
        }
    }

    private String validateRegistration(String username, String email, String password, String confirmPassword, String role) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required.";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required.";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Invalid email format.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        if (role == null || role.trim().isEmpty() || !role.matches("tourist|registered_user")) {
            return "Invalid role selected.";
        }
        return null; // No errors
    }

    private String checkForDuplicates(String username, String email) {
        // Check if username already exists
        User existingUser = controller.getUserByUsername(username);
        if (existingUser != null) {
            return "Username already exists.";
        }

        // Check if email already exists
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (var conn = utility.DatabaseConnection.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return "Email already exists.";
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking for duplicate email: " + e.getMessage());
            return "Error checking email availability.";
        }
        return null; // No duplicates
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/user-side/register.jsp");
    }
}