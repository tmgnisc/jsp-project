package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Music;
import model.MusicComment;
import model.User; // Ensure this import exists
import controller.MusicCommentController;
import controller.MusicControllerImplements;
import utility.DatabaseConnection;

/**
 * Servlet implementation class UserMusicServlet
 */
@WebServlet({"/music", "/music-detail"})
public class UserMusicServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MusicControllerImplements musicController;
    private MusicCommentController commentController;

    @Override
    public void init() throws ServletException {
        musicController = new MusicControllerImplements();
        commentController = new MusicCommentController();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/music".equals(path)) {
            String searchQuery = request.getParameter("search");
            String genre = request.getParameter("genre");

            List<Music> musicList = musicController.getAllData();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                musicList = musicList.stream()
                        .filter(music -> music.getArtistName() != null && music.getArtistName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            String selectedGenre = (genre != null && !genre.trim().isEmpty()) ? genre.toLowerCase() : "all";
            if (!"all".equals(selectedGenre)) {
                musicList = musicList.stream()
                        .filter(music -> music.getGenre() != null && music.getGenre().toLowerCase().equals(selectedGenre))
                        .collect(Collectors.toList());
            }

            request.setAttribute("musicList", musicList);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedGenre", selectedGenre);
            request.getRequestDispatcher("/user-side/music.jsp").forward(request, response);
        } else if ("/music-detail".equals(path)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int id = Integer.parseInt(idParam);
                    List<Music> musicList = musicController.getMusicById(id);
                    if (!musicList.isEmpty()) {
                        Music music = musicList.get(0);
                        List<MusicComment> comments = commentController.getCommentsByMusicId(id);
                        request.setAttribute("music", music);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.getRequestDispatcher("/user-side/music-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/music?error=music_not_found");
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    response.sendRedirect(request.getContextPath() + "/music?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/music?error=invalid_id");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/music-detail".equals(path)) {
            String idParam = request.getParameter("musicId");
            String commentText = request.getParameter("commentText");

            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                request.getSession().setAttribute("redirectAfterLogin", request.getRequestURI() + "?id=" + idParam);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (idParam != null && commentText != null && !commentText.trim().isEmpty()) {
                try {
                    int musicId = Integer.parseInt(idParam);
                    boolean success = commentController.addComment(
                        musicId,
                        user.getId(),
                        user.getUsername(),
                        commentText.trim()
                    );
                    if (success) {
                        // Refresh the page with the updated comments
                        response.sendRedirect(request.getContextPath() + "/music-detail?id=" + musicId);
                    } else {
                        // Set error and forward to JSP
                        List<Music> musicList = musicController.getMusicById(musicId);
                        Music music = musicList.isEmpty() ? null : musicList.get(0);
                        List<MusicComment> comments = commentController.getCommentsByMusicId(musicId);
                        request.setAttribute("music", music);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/music-detail.jsp").forward(request, response);
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/music?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/music-detail?id=" + idParam + "&error=invalid_input");
            }
        } else {
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
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
        return -1; // Indicates failure to find user
    }
}