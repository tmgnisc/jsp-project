package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.Music;
import utility.DatabaseConnection;

public class MusicControllerImplements implements MusicController {

    public MusicControllerImplements() {}

    private boolean ensureConnection() {
        Connection conn = DatabaseConnection.getConnection();
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addMusic(Music m) {
        if (!ensureConnection()) {
            System.err.println("Cannot add music: Database connection is not available.");
            return false;
        }

        String sql = "INSERT INTO music (artist_name, genre, formation_year, description, popular_songs, achievements, youtube_channel_url, image, celebrity_ids) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getArtistName());
            pstmt.setString(2, m.getGenre());
            pstmt.setInt(3, m.getFormationYear());
            pstmt.setString(4, m.getDescription());
            pstmt.setString(5, m.getPopularSongs());
            pstmt.setString(6, m.getAchievements());
            pstmt.setString(7, m.getYoutubeChannelUrl());
            pstmt.setString(8, m.getImage());
            // Convert List<Integer> to comma-separated string
            String celebrityIdsStr = m.getCelebrityIds() != null ? 
                m.getCelebrityIds().stream().map(String::valueOf).collect(Collectors.joining(",")) : null;
            pstmt.setString(9, celebrityIdsStr);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding music: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Music> getAllData() {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve music: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Music> musicList = new ArrayList<>();
        String sql = "SELECT * FROM music";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Music item = new Music();
                item.setId(rs.getInt("id"));
                item.setArtistName(rs.getString("artist_name"));
                item.setGenre(rs.getString("genre"));
                item.setFormationYear(rs.getInt("formation_year"));
                item.setDescription(rs.getString("description"));
                item.setPopularSongs(rs.getString("popular_songs"));
                item.setAchievements(rs.getString("achievements"));
                item.setYoutubeChannelUrl(rs.getString("youtube_channel_url"));
                item.setImage(rs.getString("image"));
                // Parse celebrity_ids into List<Integer>
                String celebrityIdsStr = rs.getString("celebrity_ids");
                List<Integer> celebrityIds = (celebrityIdsStr != null && !celebrityIdsStr.isEmpty()) ?
                    Arrays.stream(celebrityIdsStr.split(","))
                          .map(String::trim)
                          .map(Integer::parseInt)
                          .collect(Collectors.toList()) :
                    new ArrayList<>();
                item.setCelebrityIds(celebrityIds);
                musicList.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving music: " + e.getMessage());
            e.printStackTrace();
        }
        return musicList;
    }

    @Override
    public boolean deleteMusic(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot delete music: Database connection is not available.");
            return false;
        }

        String sql = "DELETE FROM music WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting music: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Music> getMusicById(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve music by ID: Database connection is not available.");
            return new ArrayList<>();
        }

        List<Music> musicList = new ArrayList<>();
        String sql = "SELECT * FROM music WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Music item = new Music();
                    item.setId(rs.getInt("id"));
                    item.setArtistName(rs.getString("artist_name"));
                    item.setGenre(rs.getString("genre"));
                    item.setFormationYear(rs.getInt("formation_year"));
                    item.setDescription(rs.getString("description"));
                    item.setPopularSongs(rs.getString("popular_songs"));
                    item.setAchievements(rs.getString("achievements"));
                    item.setYoutubeChannelUrl(rs.getString("youtube_channel_url"));
                    item.setImage(rs.getString("image"));
                    // Parse celebrity_ids into List<Integer>
                    String celebrityIdsStr = rs.getString("celebrity_ids");
                    List<Integer> celebrityIds = (celebrityIdsStr != null && !celebrityIdsStr.isEmpty()) ?
                        Arrays.stream(celebrityIdsStr.split(","))
                              .map(String::trim)
                              .map(Integer::parseInt)
                              .collect(Collectors.toList()) :
                        new ArrayList<>();
                    item.setCelebrityIds(celebrityIds);
                    musicList.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving music by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return musicList;
    }

    @Override
    public boolean editMusic(Music m) {
        if (!ensureConnection()) {
            System.err.println("Cannot edit music: Database connection is not available.");
            return false;
        }

        String sql = "UPDATE music SET artist_name = ?, genre = ?, formation_year = ?, description = ?, popular_songs = ?, achievements = ?, youtube_channel_url = ?, image = ?, celebrity_ids = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getArtistName());
            pstmt.setString(2, m.getGenre());
            pstmt.setInt(3, m.getFormationYear());
            pstmt.setString(4, m.getDescription());
            pstmt.setString(5, m.getPopularSongs());
            pstmt.setString(6, m.getAchievements());
            pstmt.setString(7, m.getYoutubeChannelUrl());
            pstmt.setString(8, m.getImage());
            // Convert List<Integer> to comma-separated string
            String celebrityIdsStr = m.getCelebrityIds() != null ? 
                m.getCelebrityIds().stream().map(String::valueOf).collect(Collectors.joining(",")) : null;
            pstmt.setString(9, celebrityIdsStr);
            pstmt.setInt(10, m.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing music: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalMusic() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM music";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Music> getTopMusic(int limit) {
        List<Music> musicList = new ArrayList<>();
        String sql = "SELECT id, artist_name, genre, formation_year, description, popular_songs, achievements, youtube_channel_url, image, celebrity_ids " +
                     "FROM music ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String celebrityIdsStr = rs.getString("celebrity_ids");
                    List<Integer> celebrityIds = (celebrityIdsStr != null && !celebrityIdsStr.isEmpty()) ?
                        Arrays.stream(celebrityIdsStr.split(","))
                              .map(String::trim)
                              .map(Integer::parseInt)
                              .collect(Collectors.toList()) :
                        new ArrayList<>();
                    Music music = new Music(
                        rs.getInt("id"),
                        rs.getString("artist_name"),
                        rs.getString("genre"),
                        rs.getInt("formation_year"),
                        rs.getString("description"),
                        rs.getString("popular_songs"),
                        rs.getString("achievements"),
                        rs.getString("youtube_channel_url"),
                        rs.getString("image"),
                        celebrityIds
                    );
                    musicList.add(music);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicList;
    }
}