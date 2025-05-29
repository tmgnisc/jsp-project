package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Comment;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

public class CommentController {

    // Constructor to ensure the table exists
    public CommentController() {
        DynamicTableCreator.createTableFromModel(Comment.class, "comments");
    }

    // Add a new comment with input validation
    public boolean addComment(int foodId, int userId, String username, String commentText) {
        // Input validation
        if (foodId <= 0 || userId <= 0) {
            System.err.println("Invalid foodId or userId: foodId=" + foodId + ", userId=" + userId);
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Username cannot be null or empty");
            return false;
        }
        if (commentText == null || commentText.trim().isEmpty()) {
            System.err.println("Comment text cannot be null or empty");
            return false;
        }

        String sql = "INSERT INTO comments (food_id, user_id, username, comment_text) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, username.trim());
            pstmt.setString(4, commentText.trim());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
            return false;
        }
    }

    // Get all comments for a specific food item
    public List<Comment> getCommentsByFoodId(int foodId) {
        if (foodId <= 0) {
            System.err.println("Invalid foodId: " + foodId);
            return new ArrayList<>();
        }

        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE food_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(
                        rs.getInt("id"),
                        rs.getInt("food_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("comment_text"),
                        rs.getTimestamp("created_at")
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching comments for foodId " + foodId + ": " + e.getMessage());
        }
        return comments;
    }
}