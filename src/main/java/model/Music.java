package model;



public class Music {
    private int id;
    private String artistName;
    private String genre;
    private int formationYear;
    private String description;
    private String popularSongs;
    private String achievements;
    private String youtubeChannelUrl;
    private String image;

    public Music() {}

    public Music(int id, String artistName, String genre, int formationYear, String description,
                 String popularSongs, String achievements, String youtubeChannelUrl, String image) {
        this.id = id;
        this.artistName = artistName;
        this.genre = genre;
        this.formationYear = formationYear;
        this.description = description;
        this.popularSongs = popularSongs;
        this.achievements = achievements;
        this.youtubeChannelUrl = youtubeChannelUrl;
        this.image = image;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getFormationYear() { return formationYear; }
    public void setFormationYear(int formationYear) { this.formationYear = formationYear; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPopularSongs() { return popularSongs; }
    public void setPopularSongs(String popularSongs) { this.popularSongs = popularSongs; }
    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }
    public String getYoutubeChannelUrl() { return youtubeChannelUrl; }
    public void setYoutubeChannelUrl(String youtubeChannelUrl) { this.youtubeChannelUrl = youtubeChannelUrl; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}