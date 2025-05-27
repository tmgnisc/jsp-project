package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Attraction;
import utility.DatabaseConnection;

public class AttractionControllerImplements implements AttractionController {

    public AttractionControllerImplements() {
    
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
    public boolean addAttraction(Attraction a) {
        if (!ensureConnection()) {
            System.err.println("Cannot add attraction: Database connection is not available.");
            return false;
        }

        String sql = "INSERT INTO attractions (name, location, description, category, image, best_time_to_visit, how_to_reach, entry_fee, opening_hours, nearby_attractions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getName());
            pstmt.setString(2, a.getLocation());
            pstmt.setString(3, a.getDescription());
            pstmt.setString(4, a.getCategory());
            pstmt.setString(5, a.getImage());
            pstmt.setString(6, a.getBestTimeToVisit());
            pstmt.setString(7, a.getHowToReach());
            pstmt.setString(8, a.getEntryFee());
            pstmt.setString(9, a.getOpeningHours());
            pstmt.setString(10, a.getNearbyAttractions());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding attraction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Attraction> getAllData() {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve attractions: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Attraction> attractions = new ArrayList<>();
        String sql = "SELECT * FROM attractions";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Attraction item = new Attraction();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setLocation(rs.getString("location"));
                item.setDescription(rs.getString("description"));
                item.setCategory(rs.getString("category"));
                item.setImage(rs.getString("image"));
                item.setBestTimeToVisit(rs.getString("best_time_to_visit"));
                item.setHowToReach(rs.getString("how_to_reach"));
                item.setEntryFee(rs.getString("entry_fee"));
                item.setOpeningHours(rs.getString("opening_hours"));
                item.setNearbyAttractions(rs.getString("nearby_attractions"));
                attractions.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving attractions: " + e.getMessage());
            e.printStackTrace();
        }
        return attractions;
    }

    @Override
    public boolean deleteAttraction(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot delete attraction: Database connection is not available.");
            return false;
        }

        String sql = "DELETE FROM attractions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting attraction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Attraction> getAttractionById(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve attraction by ID: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Attraction> attractions = new ArrayList<>();
        String sql = "SELECT * FROM attractions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Attraction item = new Attraction();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setLocation(rs.getString("location"));
                    item.setDescription(rs.getString("description"));
                    item.setCategory(rs.getString("category"));
                    item.setImage(rs.getString("image"));
                    item.setBestTimeToVisit(rs.getString("best_time_to_visit"));
                    item.setHowToReach(rs.getString("how_to_reach"));
                    item.setEntryFee(rs.getString("entry_fee"));
                    item.setOpeningHours(rs.getString("opening_hours"));
                    item.setNearbyAttractions(rs.getString("nearby_attractions"));
                    attractions.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving attraction by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return attractions;
    }

    @Override
    public boolean editAttraction(Attraction a) {
        if (!ensureConnection()) {
            System.err.println("Cannot edit attraction: Database connection is not available.");
            return false;
        }

        String sql = "UPDATE attractions SET name = ?, location = ?, description = ?, category = ?, image = ?, best_time_to_visit = ?, how_to_reach = ?, entry_fee = ?, opening_hours = ?, nearby_attractions = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getName());
            pstmt.setString(2, a.getLocation());
            pstmt.setString(3, a.getDescription());
            pstmt.setString(4, a.getCategory());
            pstmt.setString(5, a.getImage());
            pstmt.setString(6, a.getBestTimeToVisit());
            pstmt.setString(7, a.getHowToReach());
            pstmt.setString(8, a.getEntryFee());
            pstmt.setString(9, a.getOpeningHours());
            pstmt.setString(10, a.getNearbyAttractions());
            pstmt.setInt(11, a.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing attraction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}