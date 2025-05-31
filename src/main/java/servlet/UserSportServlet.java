package servlet;

import java.io.IOException;
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
import model.Sport;
import model.SportComment;
import model.Celebrity;
import model.User;
import controller.SportControllerImplements;
import controller.SportCommentController;
import controller.CelebrityControllerImplements;

@WebServlet({"/sports", "/sport-detail"})
public class UserSportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SportControllerImplements sportController;
    private SportCommentController commentController;
    private CelebrityControllerImplements celebrityController;

    @Override
    public void init() throws ServletException {
        sportController = new SportControllerImplements();
        commentController = new SportCommentController();
        celebrityController = new CelebrityControllerImplements();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/sports".equals(path)) {
            String searchQuery = request.getParameter("search");
            String category = request.getParameter("category");

            List<Sport> sportList = sportController.getAllData();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                sportList = sportList.stream()
                        .filter(sport -> sport.getName() != null && sport.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            String selectedCategory = (category != null && !category.trim().isEmpty()) ? category.toLowerCase() : "all";
            if (!"all".equals(selectedCategory)) {
                sportList = sportList.stream()
                        .filter(sport -> sport.getCategory() != null && sport.getCategory().toLowerCase().equals(selectedCategory))
                        .collect(Collectors.toList());
            }

            // Fetch celebrities for each sport and store in a map
            Map<Integer, List<Celebrity>> sportCelebritiesMap = new HashMap<>();
            for (Sport sport : sportList) {
                List<Integer> celebrityIds = sport.getCelebrityIds();
                List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                    celebrityController.getCelebritiesByIds(celebrityIds) : 
                    Collections.emptyList();
                sportCelebritiesMap.put(sport.getId(), celebrities != null ? celebrities : Collections.emptyList());
            }

            request.setAttribute("sportList", sportList);
            request.setAttribute("sportCelebritiesMap", sportCelebritiesMap);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedCategory", selectedCategory);
            request.getRequestDispatcher("/user-side/sports.jsp").forward(request, response);
        } else if ("/sport-detail".equals(path)) {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try {
                    int id = Integer.parseInt(idParam);
                    List<Sport> sportList = sportController.getSportById(id);
                    if (!sportList.isEmpty()) {
                        Sport sport = sportList.get(0);
                        List<SportComment> comments = commentController.getCommentsBySportId(id);
                        // Fetch celebrities for the sport
                        List<Integer> celebrityIds = sport.getCelebrityIds();
                        List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                            celebrityController.getCelebritiesByIds(celebrityIds) : 
                            Collections.emptyList();
                        request.setAttribute("sport", sport);
                        request.setAttribute("comments", comments != null ? comments : Collections.emptyList());
                        request.setAttribute("celebrities", celebrities != null ? celebrities : Collections.emptyList());
                        request.getRequestDispatcher("/user-side/sport-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/sports?error=sport_not_found");
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/sports?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/sports?error=invalid_id");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        String sportIdParam = request.getParameter("sportId");
        String commentText = request.getParameter("commentText");

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            String redirectUrl = "/sports".equals(path) ? request.getRequestURI() : request.getRequestURI() + "?id=" + sportIdParam;
            request.getSession().setAttribute("redirectAfterLogin", redirectUrl);
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (sportIdParam != null && commentText != null && !commentText.trim().isEmpty()) {
            try {
                int sportId = Integer.parseInt(sportIdParam);
                boolean success = commentController.addComment(sportId, user.getId(), user.getUsername(), commentText.trim());

                if (success) {
                    if ("/sports".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/sports");
                    } else if ("/sport-detail".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/sport-detail?id=" + sportId);
                    }
                } else {
                    if ("/sports".equals(path)) {
                        List<Sport> sportList = sportController.getAllData();
                        Map<Integer, List<Celebrity>> sportCelebritiesMap = new HashMap<>();
                        for (Sport sport : sportList) {
                            List<Integer> celebrityIds = sport.getCelebrityIds();
                            List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                                celebrityController.getCelebritiesByIds(celebrityIds) : 
                                Collections.emptyList();
                            sportCelebritiesMap.put(sport.getId(), celebrities != null ? celebrities : Collections.emptyList());
                        }
                        request.setAttribute("sportList", sportList);
                        request.setAttribute("sportCelebritiesMap", sportCelebritiesMap);
                        request.setAttribute("error_" + sportId, "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/sports.jsp").forward(request, response);
                    } else if ("/sport-detail".equals(path)) {
                        List<Sport> sportList = sportController.getSportById(sportId);
                        Sport sport = sportList.isEmpty() ? null : sportList.get(0);
                        List<SportComment> comments = commentController.getCommentsBySportId(sportId);
                        List<Integer> celebrityIds = sport != null ? sport.getCelebrityIds() : Collections.emptyList();
                        List<Celebrity> celebrities = (celebrityIds != null && !celebrityIds.isEmpty()) ? 
                            celebrityController.getCelebritiesByIds(celebrityIds) : 
                            Collections.emptyList();
                        request.setAttribute("sport", sport);
                        request.setAttribute("comments", comments != null ? comments : Collections.emptyList());
                        request.setAttribute("celebrities", celebrities != null ? celebrities : Collections.emptyList());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/sport-detail.jsp").forward(request, response);
                    }
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/sports?error=invalid_id");
            }
        } else {
            if ("/sports".equals(path)) {
                response.sendRedirect(request.getContextPath() + "/sports?error=invalid_input");
            } else if ("/sport-detail".equals(path) && sportIdParam != null) {
                response.sendRedirect(request.getContextPath() + "/sport-detail?id=" + sportIdParam + "&error=invalid_input");
            } else {
                response.sendRedirect(request.getContextPath() + "/sports?error=invalid_input");
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}