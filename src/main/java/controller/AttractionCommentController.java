package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.AttractionComment;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

public class AttractionCommentController {

    public AttractionCommentController() {
        DynamicTableCreator.createTableFromModel(AttractionComment.class, "attraction_comments");
        ensureSchema();
    }

    // Ensure the table has the correct schema
    private void ensureSchema() {
        String alterSql = "ALTER TABLE attraction_comments MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(alterSql)) {
            pstmt.execute();
            System.out.println("Ensured attraction_comments table schema with DEFAULT CURRENT_TIMESTAMP for created_at.");
        } catch (SQLException e) {
            System.err.println("Warning: Could not alter attraction_comments table schema: " + e.getMessage());
        }
    }

    public boolean addComment(int attractionId, int userId, String username, String commentText) {
        if (attractionId <= 0 || userId <= 0) {
            System.err.println("Invalid attractionId or userId: attractionId=" + attractionId + ", userId=" + userId);
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

        String sql = "INSERT INTO attraction_comments (attraction_id, user_id, username, comment_text, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attractionId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, username.trim());
            pstmt.setString(4, commentText.trim());
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Comment added successfully for attractionId: " + attractionId);
                return true;
            } else {
                System.err.println("No rows affected when adding comment for attractionId: " + attractionId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error adding comment for attractionId " + attractionId + ": " + e.getMessage());
            return false;
        }
    }

    public List<AttractionComment> getCommentsByAttractionId(int attractionId) {
        if (attractionId <= 0) {
            System.err.println("Invalid attractionId: " + attractionId);
            return new ArrayList<>();
        }

        List<AttractionComment> comments = new ArrayList<>();
        String sql = "SELECT * FROM attraction_comments WHERE attraction_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attractionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt == null) {
                        createdAt = new Timestamp(System.currentTimeMillis());
                        System.err.println("Warning: created_at was null for comment ID " + rs.getInt("id") + ". Using current timestamp.");
                    }
                    AttractionComment comment = new AttractionComment(
                        rs.getInt("id"),
                        rs.getInt("attraction_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("comment_text"),
                        createdAt
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching comments for attractionId " + attractionId + ": " + e.getMessage());
        }
        return comments;
    }
}