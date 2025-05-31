package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Celebrity;
import model.Movie;
import model.Sport;
import model.Music;
import utility.DatabaseConnection;

@WebServlet("/celebrity-details")
public class CelebrityDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Celebrity celebrity = null;
        List<Movie> movies = new ArrayList<>();
        List<Sport> sports = new ArrayList<>();
        List<Music> musics = new ArrayList<>();

        try {
            int id = Integer.parseInt(idParam);
            celebrity = fetchCelebrityById(id);
            if (celebrity != null) {
                movies = fetchMoviesByCelebrityId(id);
                sports = fetchSportsByCelebrityId(id);
//                musics = fetchMusicByCelebrityId(id);
            } else {
                request.setAttribute("error", "Celebrity not found.");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid celebrity ID.");
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        request.setAttribute("celebrity", celebrity);
        request.setAttribute("movies", movies);
        request.setAttribute("sports", sports);
        request.setAttribute("musics", musics);
        request.getRequestDispatcher("/user-side/celebrity-details.jsp").forward(request, response);
    }

    private Celebrity fetchCelebrityById(int id) throws SQLException {
        String sql = "SELECT id, name, bio, image FROM celebrities WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Celebrity(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        rs.getString("image")
                    );
                }
            }
        }
        return null;
    }

    private List<Movie> fetchMoviesByCelebrityId(int celebrityId) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT id, title, description, genre, rating, image, celebrity_ids FROM movies";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String celebrityIds = rs.getString("celebrity_ids");
                if (celebrityIds != null && !celebrityIds.trim().isEmpty()) {
                    String[] ids = celebrityIds.split(",");
                    for (String id : ids) {
                        try {
                            if (Integer.parseInt(id.trim()) == celebrityId) {
                                Movie movie = new Movie();
                                movie.setId(rs.getInt("id"));
                                movie.setTitle(rs.getString("title"));
                                movie.setDescription(rs.getString("description"));
                                movie.setGenre(rs.getString("genre"));
                                movie.setRating(rs.getFloat("rating"));
                                movie.setImage(rs.getString("image"));
                                movies.add(movie);
                                break;
                            }
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
            }
        }
        return movies;
    }

    private List<Sport> fetchSportsByCelebrityId(int celebrityId) throws SQLException {
        List<Sport> sports = new ArrayList<>();
        String sql = "SELECT id, name, description, category, status, image, celebrity_ids FROM sports";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String celebrityIds = rs.getString("celebrity_ids");
                if (celebrityIds != null && !celebrityIds.trim().isEmpty()) {
                    String[] ids = celebrityIds.split(",");
                    for (String id : ids) {
                        try {
                            if (Integer.parseInt(id.trim()) == celebrityId) {
                                Sport sport = new Sport();
                                sport.setId(rs.getInt("id"));
                                sport.setName(rs.getString("name"));
                                sport.setDescription(rs.getString("description"));
                                sport.setCategory(rs.getString("category"));
                                sport.setStatus(rs.getString("status"));
                                sport.setImage(rs.getString("image"));
                                sports.add(sport);
                                break;
                            }
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
            }
        }
        return sports;
    }

//    private List<Music> fetchMusicByCelebrityId(int celebrityId) throws SQLException {
//        List<Music> musics = new ArrayList<>();
//        String sql = "SELECT id, title, description, genre, image, celebrity_ids FROM musics";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//            while (rs.next()) {
//                String celebrityIds = rs.getString("celebrity_ids");
//                if (celebrityIds != null && !celebrityIds.trim().isEmpty()) {
//                    String[] ids = celebrityIds.split(",");
//                    for (String id : ids) {
//                        try {
//                            if (Integer.parseInt(id.trim()) == celebrityId) {
//                                Music music = new Music();
//                                music.setId(rs.getInt("id"));
////                                music.setTitle(rs.getString("title"));
//                                music.setDescription(rs.getString("description"));
//                                music.setGenre(rs.getString("genre"));
//                                music.setImage(rs.getString("image"));
//                                musics.add(music);
//                                break;
//                            }
//                        } catch (NumberFormatException e) {
//                            continue;
//                        }
//                    }
//                }
//            }
//        }
//        return musics;
//    }
}