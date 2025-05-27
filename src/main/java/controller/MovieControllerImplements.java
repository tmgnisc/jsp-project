package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

        String sql = "INSERT INTO movies (title, description, genre, rating, trailer_url, ticket_booking_url, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getDescription());
            pstmt.setString(3, m.getGenre());
            pstmt.setFloat(4, m.getRating());
            pstmt.setString(5, m.getTrailerUrl());
            pstmt.setString(6, m.getTicketBookingUrl());
            pstmt.setString(7, m.getImage());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
            e.printStackTrace();
            return false;
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
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting movie: " + e.getMessage());
            e.printStackTrace();
            return false;
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

        String sql = "UPDATE movies SET title = ?, description = ?, genre = ?, rating = ?, trailer_url = ?, ticket_booking_url = ?, image = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getTitle());
            pstmt.setString(2, m.getDescription());
            pstmt.setString(3, m.getGenre());
            pstmt.setFloat(4, m.getRating());
            pstmt.setString(5, m.getTrailerUrl());
            pstmt.setString(6, m.getTicketBookingUrl());
            pstmt.setString(7, m.getImage());
            pstmt.setInt(8, m.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing movie: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}