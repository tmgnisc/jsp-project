package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.Sport;
import utility.DatabaseConnection;

public class SportControllerImplements implements SportController {

    public SportControllerImplements() {}

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

        String sql = "INSERT INTO sports (name, description, category, status, history, rules, image, celebrity_ids) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, s.getName());
            pstmt.setString(2, s.getDescription());
            pstmt.setString(3, s.getCategory());
            pstmt.setString(4, s.getStatus());
            pstmt.setString(5, s.getHistory());
            pstmt.setString(6, s.getRules());
            pstmt.setString(7, s.getImage());
            String celebrityIdsStr = s.getCelebrityIds().stream()
                                     .map(String::valueOf)
                                     .collect(Collectors.joining(","));
            pstmt.setString(8, celebrityIdsStr.isEmpty() ? null : celebrityIdsStr);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    s.setId(generatedKeys.getInt(1));
                }
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding sport: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
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

                // Parse celebrity_ids
                String celebIdsStr = rs.getString("celebrity_ids");
                List<Integer> celebrityIds = new ArrayList<>();
                if (celebIdsStr != null && !celebIdsStr.isEmpty()) {
                    celebrityIds = Arrays.stream(celebIdsStr.split(","))
                                        .map(String::trim)
                                        .filter(id -> !id.isEmpty())
                                        .map(Integer::parseInt)
                                        .collect(Collectors.toList());
                }
                item.setCelebrityIds(celebrityIds);

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
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting sport: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
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

                    // Parse celebrity_ids
                    String celebIdsStr = rs.getString("celebrity_ids");
                    List<Integer> celebrityIds = new ArrayList<>();
                    if (celebIdsStr != null && !celebIdsStr.isEmpty()) {
                        celebrityIds = Arrays.stream(celebIdsStr.split(","))
                                            .map(String::trim)
                                            .filter(celebId -> !celebId.isEmpty()) // Changed 'id' to 'celebId'
                                            .map(Integer::parseInt)
                                            .collect(Collectors.toList());
                    }
                    item.setCelebrityIds(celebrityIds);

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

        String sql = "UPDATE sports SET name = ?, description = ?, category = ?, status = ?, history = ?, rules = ?, image = ?, celebrity_ids = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, s.getName());
            pstmt.setString(2, s.getDescription());
            pstmt.setString(3, s.getCategory());
            pstmt.setString(4, s.getStatus());
            pstmt.setString(5, s.getHistory());
            pstmt.setString(6, s.getRules());
            pstmt.setString(7, s.getImage());
            String celebrityIdsStr = s.getCelebrityIds().stream()
                                     .map(String::valueOf)
                                     .collect(Collectors.joining(","));
            pstmt.setString(8, celebrityIdsStr.isEmpty() ? null : celebrityIdsStr);
            pstmt.setInt(9, s.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            System.err.println("Error editing sport: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public int getTotalSports() {
        if (!ensureConnection()) {
            System.err.println("Cannot count sports: Database connection is not available.");
            return 0;
        }

        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM sports";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting sports: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    public List<Sport> getTopSports(int limit) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve top sports: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Sport> sportsList = new ArrayList<>();
        String sql = "SELECT * FROM sports ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sport sport = new Sport();
                    sport.setId(rs.getInt("id"));
                    sport.setName(rs.getString("name"));
                    sport.setDescription(rs.getString("description"));
                    sport.setCategory(rs.getString("category"));
                    sport.setStatus(rs.getString("status"));
                    sport.setHistory(rs.getString("history"));
                    sport.setRules(rs.getString("rules"));
                    sport.setImage(rs.getString("image"));

                    // Parse celebrity_ids
                    String celebIdsStr = rs.getString("celebrity_ids");
                    List<Integer> celebrityIds = new ArrayList<>();
                    if (celebIdsStr != null && !celebIdsStr.isEmpty()) {
                        celebrityIds = Arrays.stream(celebIdsStr.split(","))
                                            .map(String::trim)
                                            .filter(id -> !id.isEmpty())
                                            .map(Integer::parseInt)
                                            .collect(Collectors.toList());
                    }
                    sport.setCelebrityIds(celebrityIds);

                    sportsList.add(sport);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top sports: " + e.getMessage());
            e.printStackTrace();
        }
        return sportsList;
    }
}