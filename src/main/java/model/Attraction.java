package model;

public class Attraction {
    private int id;
    private String name;
    private String location;
    private String description;
    private String category;
    private String image;
    private String bestTimeToVisit;
    private String howToReach;
    private String entryFee;
    private String openingHours;
    private String nearbyAttractions;

    public Attraction() {}

    public Attraction(int id, String name, String location, String description, String category, String image,
                      String bestTimeToVisit, String howToReach, String entryFee, String openingHours,
                      String nearbyAttractions) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.category = category;
        this.image = image;
        this.bestTimeToVisit = bestTimeToVisit;
        this.howToReach = howToReach;
        this.entryFee = entryFee;
        this.openingHours = openingHours;
        this.nearbyAttractions = nearbyAttractions;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getBestTimeToVisit() { return bestTimeToVisit; }
    public void setBestTimeToVisit(String bestTimeToVisit) { this.bestTimeToVisit = bestTimeToVisit; }
    public String getHowToReach() { return howToReach; }
    public void setHowToReach(String howToReach) { this.howToReach = howToReach; }
    public String getEntryFee() { return entryFee; }
    public void setEntryFee(String entryFee) { this.entryFee = entryFee; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public String getNearbyAttractions() { return nearbyAttractions; }
    public void setNearbyAttractions(String nearbyAttractions) { this.nearbyAttractions = nearbyAttractions; }
}