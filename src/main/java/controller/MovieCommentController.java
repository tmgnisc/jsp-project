package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.MovieComment;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

public class MovieCommentController {
	
	public MovieCommentController() {
		DynamicTableCreator.createTableFromModel(MovieComment.class, "movie_comments");
        ensureSchema();
    }

    private void ensureSchema() {
        String alterSql = "ALTER TABLE movie_comments MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(alterSql)) {
            pstmt.execute();
            System.out.println("Ensured movie_comments table schema with DEFAULT CURRENT_TIMESTAMP for created_at.");
        } catch (SQLException e) {
            System.err.println("Warning: Could not alter movie_comments table schema: " + e.getMessage());
        }
    }

    public List<MovieComment> getCommentsByMovieId(int movieId) {
        List<MovieComment> comments = new ArrayList<>();
        String sql = "SELECT * FROM movie_comments WHERE movie_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MovieComment comment = new MovieComment();
                    comment.setId(rs.getInt("id"));
                    comment.setMovieId(rs.getInt("movie_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setUsername(rs.getString("username"));
                    comment.setCommentText(rs.getString("comment_text"));
                    comment.setCreatedAt(rs.getString("created_at"));
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching comments: " + e.getMessage());
        }
        return comments;
    }

    public boolean addComment(int movieId, int userId, String username, String commentText) {
        String sql = "INSERT INTO movie_comments (movie_id, user_id, username, comment_text, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, username);
            pstmt.setString(4, commentText);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding movie comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}