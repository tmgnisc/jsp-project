package model;

public class FoodItem {
    private int id;
    private String name;
    private String description;
    private String category;
    private String image;
    private String ingredients;
    private String preparationMethod;
    private String servingSuggestions;
    private String culturalSignificance;

    public FoodItem() {}

    public FoodItem(int id, String name, String description, String category, String image, 
                    String ingredients, String preparationMethod, String servingSuggestions, 
                    String culturalSignificance) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = image;
        this.ingredients = ingredients;
        this.preparationMethod = preparationMethod;
        this.servingSuggestions = servingSuggestions;
        this.culturalSignificance = culturalSignificance;
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
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public String getPreparationMethod() { return preparationMethod; }
    public void setPreparationMethod(String preparationMethod) { this.preparationMethod = preparationMethod; }
    public String getServingSuggestions() { return servingSuggestions; }
    public void setServingSuggestions(String servingSuggestions) { this.servingSuggestions = servingSuggestions; }
    public String getCulturalSignificance() { return culturalSignificance; }
    public void setCulturalSignificance(String culturalSignificance) { this.culturalSignificance = culturalSignificance; }
}