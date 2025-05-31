package servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Music;
import model.MusicComment;
import model.Celebrity;
import model.User;
import controller.MusicControllerImplements;
import controller.MusicCommentController;
import controller.CelebrityControllerImplements;

@WebServlet({"/music", "/music-detail"})
public class UserMusicServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MusicControllerImplements musicController;
    private MusicCommentController commentController;
    private CelebrityControllerImplements celebrityController;

    @Override
    public void init() throws ServletException {
        musicController = new MusicControllerImplements();
        commentController = new MusicCommentController();
        celebrityController = new CelebrityControllerImplements();
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

            Map<Integer, List<Celebrity>> musicCelebritiesMap = new HashMap<>();
            for (Music music : musicList) {
                List<Integer> celebrityIds = music.getCelebrityIds();
                List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                    celebrityController.getCelebritiesByIds(celebrityIds) : 
                    Collections.emptyList();
                musicCelebritiesMap.put(music.getId(), celebrities != null ? celebrities : Collections.emptyList());
            }

            request.setAttribute("musicList", musicList);
            request.setAttribute("musicCelebritiesMap", musicCelebritiesMap);
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
                        List<Integer> celebrityIds = music.getCelebrityIds();
                        List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                            celebrityController.getCelebritiesByIds(celebrityIds) : 
                            Collections.emptyList();
                        request.setAttribute("music", music);
                        request.setAttribute("comments", comments != null ? comments : Collections.emptyList());
                        request.setAttribute("celebrities", celebrities != null ? celebrities : Collections.emptyList());
                        request.getRequestDispatcher("/user-side/music-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/music?error=music_not_found");
                    }
                } catch (NumberFormatException e) {
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
        String musicIdParam = request.getParameter("musicId");
        String commentText = request.getParameter("commentText");

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            String redirectUrl = "/music".equals(path) ? request.getRequestURI() : request.getRequestURI() + "?id=" + musicIdParam;
            request.getSession().setAttribute("redirectAfterLogin", redirectUrl);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (musicIdParam != null && commentText != null && !commentText.trim().isEmpty()) {
            try {
                int musicId = Integer.parseInt(musicIdParam);
                boolean success = commentController.addComment(musicId, user.getId(), user.getUsername(), commentText.trim());

                if (success) {
                    if ("/music".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/music");
                    } else if ("/music-detail".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/music-detail?id=" + musicId);
                    }
                } else {
                    if ("/music".equals(path)) {
                        List<Music> musicList = musicController.getAllData();
                        Map<Integer, List<Celebrity>> musicCelebritiesMap = new HashMap<>();
                        for (Music music : musicList) {
                            List<Integer> celebrityIds = music.getCelebrityIds();
                            List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                                celebrityController.getCelebritiesByIds(celebrityIds) : 
                                Collections.emptyList();
                            musicCelebritiesMap.put(music.getId(), celebrities != null ? celebrities : Collections.emptyList());
                        }
                        request.setAttribute("musicList", musicList);
                        request.setAttribute("musicCelebritiesMap", musicCelebritiesMap);
                        request.setAttribute("error_" + musicId, "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/music.jsp").forward(request, response);
                    } else if ("/music-detail".equals(path)) {
                        List<Music> musicList = musicController.getMusicById(musicId);
                        Music music = musicList.isEmpty() ? null : musicList.get(0);
                        List<MusicComment> comments = commentController.getCommentsByMusicId(musicId);
                        List<Integer> celebrityIds = music != null ? music.getCelebrityIds() : Collections.emptyList();
                        List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                            celebrityController.getCelebritiesByIds(celebrityIds) : 
                            Collections.emptyList();
                        request.setAttribute("music", music);
                        request.setAttribute("comments", comments != null ? comments : Collections.emptyList());
                        request.setAttribute("celebrities", celebrities != null ? celebrities : Collections.emptyList());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/music-detail.jsp").forward(request, response);
                    }
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/music?error=invalid_id");
            }
        } else {
            if ("/music".equals(path)) {
                response.sendRedirect(request.getContextPath() + "/music?error=invalid_input");
            } else if ("/music-detail".equals(path) && musicIdParam != null) {
                response.sendRedirect(request.getContextPath() + "/music-detail?id=" + musicIdParam + "&error=invalid_input");
            } else {
                response.sendRedirect(request.getContextPath() + "/music?error=invalid_input");
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}