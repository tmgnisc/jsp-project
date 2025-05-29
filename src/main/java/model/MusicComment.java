package model;

public class MusicComment {
    private int id;
    private int musicId;
    private int userId;
    private String username;
    private String commentText;
    private String createdAt;

    public MusicComment() {}

    public MusicComment(int id, int musicId, int userId, String username, String commentText, String createdAt) {
        this.id = id;
        this.musicId = musicId;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMusicId() { return musicId; }
    public void setMusicId(int musicId) { this.musicId = musicId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}