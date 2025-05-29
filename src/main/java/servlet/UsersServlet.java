package servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import controller.UserControllerImplements;
import model.User;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;
import org.apache.commons.codec.digest.DigestUtils;

@WebServlet("/user-dashboard")
public class UsersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserControllerImplements controller;
    private static final String STATIC_SALT = "NepalNavigatorSalt2025";

    @Override
    public void init() throws ServletException {
        DynamicTableCreator.createTableFromModel(User.class, "users");
        controller = new UserControllerImplements();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is authenticated
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("notify", "Please log in to access this page.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if user is an admin
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            request.getSession().setAttribute("notify", "Access denied. Admins only.");
            response.sendRedirect(request.getContextPath() + "/user-side/index.jsp");
            return;
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        request.setAttribute("notify", request.getSession().getAttribute("notify") != null ? request.getSession().getAttribute("notify") : "");
        request.getSession().removeAttribute("notify");

        if ("getUser".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<User> userList = controller.getUserById(id);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                if (!userList.isEmpty()) {
                    User item = userList.get(0);
                    String json = String.format(
                        "{\"id\":%d,\"fullName\":\"%s\",\"email\":\"%s\",\"username\":\"%s\",\"role\":\"%s\",\"status\":\"%s\"}",
                        item.getId(),
                        item.getFullName() != null ? item.getFullName().replace("\"", "\\\"") : "",
                        item.getEmail() != null ? item.getEmail().replace("\"", "\\\"") : "",
                        item.getUsername() != null ? item.getUsername().replace("\"", "\\\"") : "",
                        item.getRole() != null ? item.getRole().replace("\"", "\\\"") : "",
                        item.getStatus() != null ? item.getStatus().replace("\"", "\\\"") : ""
                    );
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"User not found\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid user ID\"}");
            }
            return;
        }

        if ("edit".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                List<User> userList = controller.getUserById(id);
                if (!userList.isEmpty()) {
                    request.setAttribute("userToEdit", userList.get(0));
                } else {
                    request.getSession().setAttribute("notify", "No user found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("notify", "Invalid user ID.");
            }
        }

        List<User> userList = controller.getAllData();
        request.setAttribute("userList", userList);
        request.getRequestDispatcher("/admin-side/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is authenticated
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("notify", "Please log in to access this page.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if user is an admin
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            request.getSession().setAttribute("notify", "Access denied. Admins only.");
            response.sendRedirect(request.getContextPath() + "/user-side/index.jsp");
            return;
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        String notifyMessage = "";
        if ("delete".equals(action) && idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                boolean success = controller.deleteUser(id);
                notifyMessage = success ? "User deleted successfully!" : "Failed to delete user.";
            } catch (NumberFormatException e) {
                notifyMessage = "Invalid user ID.";
            }
        } else if ("add".equals(action) || "edit".equals(action)) {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role");
            String status = request.getParameter("status");

            String hashedPassword = null;
            if (password != null && !password.trim().isEmpty()) {
                String salt = username + STATIC_SALT;
                String saltedPassword = salt + password;
                hashedPassword = DigestUtils.shaHex(saltedPassword);
            }

            if ("edit".equals(action) && idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    User updatedUser = new User(id, fullName, email, username, hashedPassword, role, status);
                    boolean success = controller.editUser(updatedUser);
                    notifyMessage = success ? "User updated successfully!" : "Failed to update user.";
                } catch (NumberFormatException e) {
                    notifyMessage = "Invalid user ID.";
                }
            } else if ("add".equals(action)) {
                if (password == null || password.trim().isEmpty()) {
                    notifyMessage = "Password is required for new users.";
                } else {
                    User newUser = new User(0, fullName, email, username, hashedPassword, role, status);
                    boolean success = controller.addUser(newUser);
                    notifyMessage = success ? "User added successfully!" : "Failed to add user.";
                }
            }
        } else {
            notifyMessage = "Invalid action.";
        }

        request.getSession().setAttribute("notify", notifyMessage);
        List<User> userList = controller.getAllData();
        request.setAttribute("userList", userList);
        request.getRequestDispatcher("/admin-side/users.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection();
        super.destroy();
    }
}