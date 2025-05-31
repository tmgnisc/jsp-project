package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.Movie;
import utility.DatabaseConnection;

public class MovieControllerImplements implements MovieController {

    public MovieControllerImplements() {
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
    public boolean addMovie(Movie m) {
        if (!ensureConnection()) {
            System.err.println("Cannot add movie: Database connection is not available.");
            return false;
        }

        String sql = "INSERT INTO movies (title, description, genre, rating, trailer_url, ticket_booking_url, image, celebrity_ids) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getDescription());
            pstmt.setString(3, m.getGenre());
            pstmt.setFloat(4, m.getRating());
            pstmt.setString(5, m.getTrailerUrl());
            pstmt.setString(6, m.getTicketBookingUrl());
            pstmt.setString(7, m.getImage());
            // Convert List<Integer> to comma-separated string
            String celebrityIdsStr = m.getCelebrityIds().stream()
                                     .map(String::valueOf)
                                     .collect(Collectors.joining(","));
            pstmt.setString(8, celebrityIdsStr.isEmpty() ? null : celebrityIdsStr);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int movieId = generatedKeys.getInt(1);
                    m.setId(movieId); // Update the movie object with the new ID
                }
                conn.commit(); // Commit transaction
                return true;
            }
            conn.rollback(); // Rollback on failure
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
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
    public List<Movie> getAllData() {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve movies: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Movie> movieList = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Movie item = new Movie();
                item.setId(rs.getInt("id"));
                item.setTitle(rs.getString("title"));
                item.setDescription(rs.getString("description"));
                item.setGenre(rs.getString("genre"));
                item.setRating(rs.getFloat("rating"));
                item.setTrailerUrl(rs.getString("trailer_url"));
                item.setTicketBookingUrl(rs.getString("ticket_booking_url"));
                item.setImage(rs.getString("image"));

                // Parse celebrity_ids from comma-separated string to List<Integer>
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

                movieList.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving movies: " + e.getMessage());
            e.printStackTrace();
        }
        return movieList;
    }

    @Override
    public boolean deleteMovie(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot delete movie: Database connection is not available.");
            return false;
        }

        String sql = "DELETE FROM movies WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting movie: " + e.getMessage());
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
    public List<Movie> getMovieById(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve movie by ID: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Movie> movieList = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Movie item = new Movie();
                    item.setId(rs.getInt("id"));
                    item.setTitle(rs.getString("title"));
                    item.setDescription(rs.getString("description"));
                    item.setGenre(rs.getString("genre"));
                    item.setRating(rs.getFloat("rating"));
                    item.setTrailerUrl(rs.getString("trailer_url"));
                    item.setTicketBookingUrl(rs.getString("ticket_booking_url"));
                    item.setImage(rs.getString("image"));

                    // Parse celebrity_ids from comma-separated string to List<Integer>
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

                    movieList.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving movie by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return movieList;
    }

    @Override
    public boolean editMovie(Movie m) {
        if (!ensureConnection()) {
            System.err.println("Cannot edit movie: Database connection is not available.");
            return false;
        }

        String sql = "UPDATE movies SET title = ?, description = ?, genre = ?, rating = ?, trailer_url = ?, ticket_booking_url = ?, image = ?, celebrity_ids = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getDescription());
            pstmt.setString(3, m.getGenre());
            pstmt.setFloat(4, m.getRating());
            pstmt.setString(5, m.getTrailerUrl());
            pstmt.setString(6, m.getTicketBookingUrl());
            pstmt.setString(7, m.getImage());
            // Convert List<Integer> to comma-separated string
            String celebrityIdsStr = m.getCelebrityIds().stream()
                                     .map(String::valueOf)
                                     .collect(Collectors.joining(","));
            pstmt.setString(8, celebrityIdsStr.isEmpty() ? null : celebrityIdsStr);
            pstmt.setInt(9, m.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            System.err.println("Error editing movie: " + e.getMessage());
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


    public int getTotalMovies() {
        if (!ensureConnection()) {
            System.err.println("Cannot count movies: Database connection is not available.");
            return 0;
        }

        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting movies: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

 
    public List<Movie> getTopMovies(int limit) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve top movies: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movie item = new Movie();
                    item.setId(rs.getInt("id"));
                    item.setTitle(rs.getString("title"));
                    item.setDescription(rs.getString("description"));
                    item.setGenre(rs.getString("genre"));
                    item.setRating(rs.getFloat("rating"));
                    item.setTrailerUrl(rs.getString("trailer_url"));
                    item.setTicketBookingUrl(rs.getString("ticket_booking_url"));
                    item.setImage(rs.getString("image"));

                    // Parse celebrity_ids from comma-separated string to List<Integer>
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

                    movies.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top movies: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }
}