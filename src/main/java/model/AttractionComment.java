package model;

import java.sql.Timestamp;

public class AttractionComment {
    private int id;
    private int attractionId;
    private int userId;
    private String username;
    private String commentText;
    private Timestamp createdAt;

    // Default Constructor
    public AttractionComment() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Parameterized Constructor
    public AttractionComment(int id, int attractionId, int userId, String username, String commentText, Timestamp createdAt) {
        this.id = id;
        this.attractionId = attractionId;
        this.userId = userId;
        this.username = (username != null) ? username.trim() : null;
        this.commentText = (commentText != null) ? commentText.trim() : null;
        this.createdAt = (createdAt != null) ? createdAt : new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(int attractionId) {
        this.attractionId = attractionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = (username != null) ? username.trim() : null;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = (commentText != null) ? commentText.trim() : null;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = (createdAt != null) ? createdAt : new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "AttractionComment{" +
                "id=" + id +
                ", attractionId=" + attractionId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", commentText='" + commentText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}