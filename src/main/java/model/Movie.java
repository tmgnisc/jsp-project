package model;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String genre;
    private float rating;
    private String trailerUrl;
    private String ticketBookingUrl;
    private String image;
    private List<Integer> celebrityIds; // References to Celebrity IDs

    public Movie() {
        this.celebrityIds = new ArrayList<>();
    }

    public Movie(int id, String title, String description, String genre, float rating,
                 String trailerUrl, String ticketBookingUrl, String image, List<Integer> celebrityIds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        setRating(rating); // Use setter for validation
        this.trailerUrl = trailerUrl;
        this.ticketBookingUrl = ticketBookingUrl;
        this.image = image;
        this.celebrityIds = celebrityIds != null ? new ArrayList<>(celebrityIds) : new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public float getRating() { return rating; }
    public void setRating(float rating) { 
        this.rating = Math.max(0.0f, Math.min(10.0f, rating)); // Constrain rating between 0 and 10
    }
    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public String getTicketBookingUrl() { return ticketBookingUrl; }
    public void setTicketBookingUrl(String ticketBookingUrl) { this.ticketBookingUrl = ticketBookingUrl; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public List<Integer> getCelebrityIds() { return new ArrayList<>(celebrityIds); } // Defensive copy on getter
    public void setCelebrityIds(List<Integer> celebrityIds) { 
        this.celebrityIds = celebrityIds != null ? new ArrayList<>(celebrityIds) : new ArrayList<>(); 
    }
    public void addCelebrityId(int celebrityId) { this.celebrityIds.add(celebrityId); }
}