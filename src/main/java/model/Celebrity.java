package model;

public class Celebrity {
    private int id;
    private String name;
    private String bio;
    private String image;

    public Celebrity() {}

    public Celebrity(int id, String name, String bio, String image) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}