package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.SportComment;
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

public class SportCommentController {
	
	public SportCommentController() {
		DynamicTableCreator.createTableFromModel(SportComment.class, "sport_comments");
        ensureSchema();
    }

    private void ensureSchema() {
        String alterSql = "ALTER TABLE sport_comments MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(alterSql)) {
            pstmt.execute();
            System.out.println("Ensured sport_comments table schema with DEFAULT CURRENT_TIMESTAMP for created_at.");
        } catch (SQLException e) {
            System.err.println("Warning: Could not alter sport_comments table schema: " + e.getMessage());
        }
    }

    public List<SportComment> getCommentsBySportId(int sportId) {
        List<SportComment> comments = new ArrayList<>();
        String sql = "SELECT * FROM sport_comments WHERE sport_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sportId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SportComment comment = new SportComment();
                    comment.setId(rs.getInt("id"));
                    comment.setSportId(rs.getInt("sport_id"));
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

    public boolean addComment(int sportId, int userId, String username, String commentText) {
        String sql = "INSERT INTO sport_comments (sport_id, user_id, username, comment_text, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sportId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, username);
            pstmt.setString(4, commentText);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding sport comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}