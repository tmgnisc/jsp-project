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
import utility.DatabaseConnection;

@WebServlet("/celebrity-details")
public class CelebrityDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Celebrity celebrity = null;
        List<Movie> movies = new ArrayList<>();

        try {
            int id = Integer.parseInt(idParam);
            celebrity = fetchCelebrityById(id);
            if (celebrity != null) {
                movies = fetchMoviesByCelebrityId(id);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid celebrity ID.");
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        request.setAttribute("celebrity", celebrity);
        request.setAttribute("movies", movies);
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
                    // Assuming celebrityIds is a comma-separated string
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
                                break; // Move to the next movie once a match is found
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid IDs in the celebrityIds string
                            continue;
                        }
                    }
                }
            }
        }
        return movies;
    }
}