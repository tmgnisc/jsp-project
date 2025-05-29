package model;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int foodId;
    private int userId;
    private String username;
    private String commentText;
    private Timestamp createdAt;

    // Constructor
    public Comment(int id, int foodId, int userId, String username, String commentText, Timestamp createdAt) {
        this.id = id;
        this.foodId = foodId;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
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
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}