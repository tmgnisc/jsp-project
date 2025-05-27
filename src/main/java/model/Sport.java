package model;

public class Sport {
    private int id;
    private String name;
    private String description;
    private String category;
    private String status;
    private String history;
    private String rules;
    private String image;

    public Sport() {}

    public Sport(int id, String name, String description, String category, String status,
                 String history, String rules, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
        this.history = history;
        this.rules = rules;
        this.image = image;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}