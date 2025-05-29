package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.MusicComment; // Correct import
import utility.DatabaseConnection;
import utility.DynamicTableCreator;

public class MusicCommentController {
	
	public MusicCommentController() {
		DynamicTableCreator.createTableFromModel(MusicComment.class, "music_comments"); // Correct model
        ensureSchema();
    }

    // Ensure the table has the correct schema
    private void ensureSchema() {
        String alterSql = "ALTER TABLE music_comments MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"; // Correct table name
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(alterSql)) {
            pstmt.execute();
            System.out.println("Ensured music_comments table schema with DEFAULT CURRENT_TIMESTAMP for created_at.");
        } catch (SQLException e) {
            System.err.println("Warning: Could not alter music_comments table schema: " + e.getMessage());
            // If table doesn't exist or column is missing, let DynamicTableCreator handle it
        }
    }

    public List<MusicComment> getCommentsByMusicId(int musicId) {
        List<MusicComment> comments = new ArrayList<>();
        String sql = "SELECT * FROM music_comments WHERE music_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, musicId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MusicComment comment = new MusicComment(
                        rs.getInt("id"),
                        rs.getInt("music_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("comment_text"),
                        rs.getString("created_at") // Ensure this matches the database format
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving music comments: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    public boolean addComment(int musicId, int userId, String username, String commentText) {
        String sql = "INSERT INTO music_comments (music_id, user_id, username, comment_text, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, username);
            pstmt.setString(4, commentText);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding music comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}