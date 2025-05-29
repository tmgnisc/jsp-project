package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Sport;
import utility.DatabaseConnection;

public class SportControllerImplements implements SportController {

    public SportControllerImplements() {
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
    public boolean addSport(Sport s) {
        if (!ensureConnection()) {
            System.err.println("Cannot add sport: Database connection is not available.");
            return false;
        }

        String sql = "INSERT INTO sports (name, description, category, status, history, rules, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getName());
            pstmt.setString(2, s.getDescription());
            pstmt.setString(3, s.getCategory());
            pstmt.setString(4, s.getStatus());
            pstmt.setString(5, s.getHistory());
            pstmt.setString(6, s.getRules());
            pstmt.setString(7, s.getImage());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding sport: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Sport> getAllData() {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve sports: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Sport> sportList = new ArrayList<>();
        String sql = "SELECT * FROM sports";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Sport item = new Sport();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setCategory(rs.getString("category"));
                item.setStatus(rs.getString("status"));
                item.setHistory(rs.getString("history"));
                item.setRules(rs.getString("rules"));
                item.setImage(rs.getString("image"));
                sportList.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving sports: " + e.getMessage());
            e.printStackTrace();
        }
        return sportList;
    }

    @Override
    public boolean deleteSport(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot delete sport: Database connection is not available.");
            return false;
        }

        String sql = "DELETE FROM sports WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting sport: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Sport> getSportById(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve sport by ID: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Sport> sportList = new ArrayList<>();
        String sql = "SELECT * FROM sports WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Sport item = new Sport();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setCategory(rs.getString("category"));
                    item.setStatus(rs.getString("status"));
                    item.setHistory(rs.getString("history"));
                    item.setRules(rs.getString("rules"));
                    item.setImage(rs.getString("image"));
                    sportList.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving sport by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return sportList;
    }

    @Override
    public boolean editSport(Sport s) {
        if (!ensureConnection()) {
            System.err.println("Cannot edit sport: Database connection is not available.");
            return false;
        }

        String sql = "UPDATE sports SET name = ?, description = ?, category = ?, status = ?, history = ?, rules = ?, image = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getName());
            pstmt.setString(2, s.getDescription());
            pstmt.setString(3, s.getCategory());
            pstmt.setString(4, s.getStatus());
            pstmt.setString(5, s.getHistory());
            pstmt.setString(6, s.getRules());
            pstmt.setString(7, s.getImage());
            pstmt.setInt(8, s.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing sport: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalSports() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM sports"; 
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
    
    public List<Sport> getTopSports(int limit) {
        List<Sport> sportsList = new ArrayList<>();
        String sql = "SELECT id, name, description, category, status, history, rules, image " +
                     "FROM sports ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sport sport = new Sport(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getString("history"),
                        rs.getString("rules"),
                        rs.getString("image")
                    );
                    sportsList.add(sport);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sportsList;
    }
}