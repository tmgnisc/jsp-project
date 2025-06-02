package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Celebrity;
import utility.DatabaseConnection;

public class CelebrityControllerImplements implements CelebrityController {

    public CelebrityControllerImplements() {}

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
    public boolean addCelebrity(Celebrity c) {
        if (!ensureConnection()) return false;
        String sql = "INSERT INTO celebrities (name, image, bio) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getImage());
            pstmt.setString(3, c.getBio());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    c.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding celebrity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Celebrity> getAllData() {
        if (!ensureConnection()) return new ArrayList<>();
        List<Celebrity> celebrityList = new ArrayList<>();
        String sql = "SELECT * FROM celebrities";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Celebrity item = new Celebrity();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setImage(rs.getString("image"));
                item.setBio(rs.getString("bio")); // Fetch bio
                celebrityList.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving celebrities: " + e.getMessage());
            e.printStackTrace();
        }
        return celebrityList;
    }

    @Override
    public boolean deleteCelebrity(int id) {
        if (!ensureConnection()) return false;
        String sql = "DELETE FROM celebrities WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting celebrity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Celebrity> getCelebrityById(int id) {
        if (!ensureConnection()) return new ArrayList<>();
        List<Celebrity> celebrityList = new ArrayList<>();
        String sql = "SELECT * FROM celebrities WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Celebrity item = new Celebrity();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setImage(rs.getString("image"));
                    item.setBio(rs.getString("bio")); // Fetch bio
                    celebrityList.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving celebrity by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return celebrityList;
    }

    @Override
    public boolean editCelebrity(Celebrity c) {
        if (!ensureConnection()) return false;
        String sql = "UPDATE celebrities SET name = ?, image = ?, bio = ? WHERE id = ?"; // Include bio in update
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getImage());
            pstmt.setString(3, c.getBio()); // Set bio
            pstmt.setInt(4, c.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing celebrity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Celebrity> getCelebritiesByIds(List<Integer> ids) {
        if (!ensureConnection() || ids == null || ids.isEmpty()) return new ArrayList<>();
        List<Celebrity> celebrityList = new ArrayList<>();
        String sql = "SELECT * FROM celebrities WHERE id IN (";
        // Build the placeholder string: ?,?,?,...
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            placeholders.append("?");
            if (i < ids.size() - 1) placeholders.append(",");
        }
        sql += placeholders.toString() + ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the parameters
            for (int i = 0; i < ids.size(); i++) {
                pstmt.setInt(i + 1, ids.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Celebrity item = new Celebrity();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setImage(rs.getString("image"));
                    item.setBio(rs.getString("bio")); // Fetch bio
                    celebrityList.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving celebrities by IDs: " + e.getMessage());
            e.printStackTrace();
        }
        return celebrityList;
    }
}