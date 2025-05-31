package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Movie;
import model.MovieComment;
import model.User;
import model.Celebrity;
import controller.MovieControllerImplements;
import controller.MovieCommentController;
import controller.CelebrityControllerImplements;
import utility.DatabaseConnection;

@WebServlet({"/movies", "/movie-detail"})
public class UserMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MovieControllerImplements movieController;
    private MovieCommentController commentController;
    private CelebrityControllerImplements celebrityController;

    @Override
    public void init() throws ServletException {
        movieController = new MovieControllerImplements();
        commentController = new MovieCommentController();
        celebrityController = new CelebrityControllerImplements();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/movies".equals(path)) {
            String searchQuery = request.getParameter("search");
            String genre = request.getParameter("genre");

            List<Movie> movieList = movieController.getAllData();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                movieList = movieList.stream()
                        .filter(movie -> movie.getTitle() != null && movie.getTitle().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            String selectedGenre = (genre != null && !genre.trim().isEmpty()) ? genre.toLowerCase() : "all";
            if (!"all".equals(selectedGenre)) {
                movieList = movieList.stream()
                        .filter(movie -> movie.getGenre() != null && movie.getGenre().toLowerCase().equals(selectedGenre))
                        .collect(Collectors.toList());
            }

            // Fetch celebrities for each movie and store in a map
            Map<Integer, List<Celebrity>> movieCelebritiesMap = new HashMap<>();
            for (Movie movie : movieList) {
                List<Integer> celebrityIds = movie.getCelebrityIds();
                List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                    celebrityController.getCelebritiesByIds(celebrityIds) : 
                    Collections.emptyList();
                movieCelebritiesMap.put(movie.getId(), celebrities != null ? celebrities : Collections.emptyList());
            }

            request.setAttribute("movieList", movieList);
            request.setAttribute("movieCelebritiesMap", movieCelebritiesMap);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedGenre", selectedGenre);
            request.getRequestDispatcher("/user-side/movie.jsp").forward(request, response);
        } else if ("/movie-detail".equals(path)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int id = Integer.parseInt(idParam);
                    List<Movie> movieList = movieController.getMovieById(id);
                    if (!movieList.isEmpty()) {
                        Movie movie = movieList.get(0);
                        List<MovieComment> comments = commentController.getCommentsByMovieId(id);
                        request.setAttribute("movie", movie);
                        request.setAttribute("comments", comments != null ? comments : Collections.emptyList());

                        // Fetch celebrities for the movie
                        List<Integer> celebrityIds = movie.getCelebrityIds();
                        List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                            celebrityController.getCelebritiesByIds(celebrityIds) : 
                            Collections.emptyList();
                        request.setAttribute("celebrities", celebrities != null ? celebrities : Collections.emptyList());

                        request.getRequestDispatcher("/user-side/movie-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/movies?error=movie_not_found");
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/movies?error=invalid_id");
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/movies?error=unexpected_error");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/movies?error=invalid_id");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        String idParam = request.getParameter("movieId");
        String commentText = request.getParameter("commentText");

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("redirectAfterLogin", request.getRequestURI() + "?id=" + idParam);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (idParam != null && commentText != null && !commentText.trim().isEmpty()) {
            try {
                int movieId = Integer.parseInt(idParam);
                boolean success = commentController.addComment(
                    movieId,
                    user.getId(),
                    user.getUsername(),
                    commentText.trim()
                );

                if (success) {
                    if ("/movies".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/movies");
                    } else if ("/movie-detail".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/movie-detail?id=" + movieId);
                    }
                } else {
                    if ("/movies".equals(path)) {
                        List<Movie> movieList = movieController.getAllData();
                        request.setAttribute("movieList", movieList);
                        request.setAttribute("error_" + movieId, "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/movies.jsp").forward(request, response);
                    } else if ("/movie-detail".equals(path)) {
                        List<Movie> movieList = movieController.getMovieById(movieId);
                        Movie movie = movieList.isEmpty() ? null : movieList.get(0);
                        List<MovieComment> comments = commentController.getCommentsByMovieId(movieId);
                        request.setAttribute("movie", movie);
                        request.setAttribute("comments", comments != null ? comments : Collections.emptyList());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/movie-detail.jsp").forward(request, response);
                    }
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/movies?error=invalid_id");
            }
        } else {
            if ("/movies".equals(path)) {
                response.sendRedirect(request.getContextPath() + "/movies?error=invalid_input");
            } else {
                response.sendRedirect(request.getContextPath() + "/movie-detail?id=" + idParam + "&error=invalid_input");
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching userId for username " + username + ": " + e.getMessage());
        }
        return -1;
    }
}