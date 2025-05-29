package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;
import utility.DatabaseConnection;

public class UserControllerImplements implements UserController {

    public UserControllerImplements() {
        // No need to initialize connection here; DatabaseConnection handles it
    }

    private boolean ensureConnection() {
        Connection conn = DatabaseConnection.getConnection();
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addUser(User u) {
        if (!ensureConnection()) {
            System.err.println("Cannot add user: Database connection is not available.");
            return false;
        }

        // Password is already hashed by the servlet
        String sql = "INSERT INTO users (full_name, email, username, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getFullName());
            pstmt.setString(2, u.getEmail());
            pstmt.setString(3, u.getUsername());
            pstmt.setString(4, u.getPassword()); // Store the pre-hashed password
            pstmt.setString(5, u.getRole());
            pstmt.setString(6, u.getStatus());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getAllData() {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve users: Database connection is not available.");
            return new ArrayList<>();
        }

        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User item = new User();
                item.setId(rs.getInt("id"));
                item.setFullName(rs.getString("full_name"));
                item.setEmail(rs.getString("email"));
                item.setUsername(rs.getString("username"));
                // Do not set password for security reasons
                item.setRole(rs.getString("role"));
                item.setStatus(rs.getString("status"));
                userList.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public boolean deleteUser(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot delete user: Database connection is not available.");
            return false;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getUserById(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve user by ID: Database connection is not available.");
            return new ArrayList<>();
        }

        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User item = new User();
                    item.setId(rs.getInt("id"));
                    item.setFullName(rs.getString("full_name"));
                    item.setEmail(rs.getString("email"));
                    item.setUsername(rs.getString("username"));
                    // Do not set password for security reasons
                    item.setRole(rs.getString("role"));
                    item.setStatus(rs.getString("status"));
                    userList.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public boolean editUser(User u) {
        if (!ensureConnection()) {
            System.err.println("Cannot edit user: Database connection is not available.");
            return false;
        }

        String sql;
        PreparedStatement pstmt;
        try (Connection conn = DatabaseConnection.getConnection()) {
            // If password is provided (not null or empty), update it; otherwise, exclude from update
            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                sql = "UPDATE users SET full_name = ?, email = ?, username = ?, password = ?, role = ?, status = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, u.getFullName());
                pstmt.setString(2, u.getEmail());
                pstmt.setString(3, u.getUsername());
                pstmt.setString(4, u.getPassword()); // Store the pre-hashed password
                pstmt.setString(5, u.getRole());
                pstmt.setString(6, u.getStatus());
                pstmt.setInt(7, u.getId());
            } else {
                sql = "UPDATE users SET full_name = ?, email = ?, username = ?, role = ?, status = ? WHERE id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, u.getFullName());
                pstmt.setString(2, u.getEmail());
                pstmt.setString(3, u.getUsername());
                pstmt.setString(4, u.getRole());
                pstmt.setString(5, u.getStatus());
                pstmt.setInt(6, u.getId());
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Add method to fetch user by username for login (if needed)
    public User getUserByUsername(String username) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve user by username: Database connection is not available.");
            return null;
        }

        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User item = new User();
                    item.setId(rs.getInt("id"));
                    item.setFullName(rs.getString("full_name"));
                    item.setEmail(rs.getString("email"));
                    item.setUsername(rs.getString("username"));
                    // Do not set password for security reasons
                    item.setRole(rs.getString("role"));
                    item.setStatus(rs.getString("status"));
                    return item;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public User getUserByEmail(String email) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve user by email: Database connection is not available.");
            return null;
        }

        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User item = new User();
                    item.setId(rs.getInt("id"));
                    item.setFullName(rs.getString("full_name"));
                    item.setEmail(rs.getString("email"));
                    item.setUsername(rs.getString("username"));
                    // Do not set password for security reasons
                    item.setRole(rs.getString("role"));
                    item.setStatus(rs.getString("status"));
                    return item;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public int getTotalUsers() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM users"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}