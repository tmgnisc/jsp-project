package model;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String genre;
    private float rating;
    private String trailerUrl;
    private String ticketBookingUrl;
    private String image;

    public Movie() {}

    public Movie(int id, String title, String description, String genre, float rating,
                 String trailerUrl, String ticketBookingUrl, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.rating = rating;
        this.trailerUrl = trailerUrl;
        this.ticketBookingUrl = ticketBookingUrl;
        this.image = image;
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
    public void setRating(float rating) { this.rating = rating; }
    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public String getTicketBookingUrl() { return ticketBookingUrl; }
    public void setTicketBookingUrl(String ticketBookingUrl) { this.ticketBookingUrl = ticketBookingUrl; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}