package servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Sport;
import model.SportComment;
import model.User;
import controller.SportControllerImplements;
import controller.SportCommentController;

@WebServlet({"/sport", "/sport-detail"})
public class UserSportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SportControllerImplements sportController;
    private SportCommentController commentController;

    @Override
    public void init() throws ServletException {
        sportController = new SportControllerImplements();
        commentController = new SportCommentController();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/sport".equals(path)) {
            String searchQuery = request.getParameter("search");
            String category = request.getParameter("category");

            List<Sport> sportList = sportController.getAllData();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String query = searchQuery.toLowerCase();
                sportList = sportList.stream()
                        .filter(sport -> sport.getName() != null && sport.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());
            }

            if (category != null && !category.trim().isEmpty() && !"all".equals(category.toLowerCase())) {
                sportList = sportList.stream()
                        .filter(sport -> sport.getCategory() != null && sport.getCategory().toLowerCase().equals(category.toLowerCase()))
                        .collect(Collectors.toList());
            }

            for (Sport sport : sportList) {
                List<SportComment> comments = commentController.getCommentsBySportId(sport.getId());
                request.setAttribute("comments_" + sport.getId(), comments);
            }

            request.setAttribute("sportList", sportList);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedCategory", category);
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
                        request.setAttribute("sport", sport);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.getRequestDispatcher("/user-side/sport-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/sport?error=sport_not_found");
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/sport?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/sport?error=invalid_id");
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
            request.getSession().setAttribute("redirectAfterLogin", request.getRequestURI());
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (sportIdParam != null && commentText != null && !commentText.trim().isEmpty()) {
            try {
                int sportId = Integer.parseInt(sportIdParam);
                boolean success = commentController.addComment(sportId, user.getId(), user.getUsername(), commentText.trim());

                if (success) {
                    if ("/sport".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/sport");
                    } else if ("/sport-detail".equals(path)) {
                        response.sendRedirect(request.getContextPath() + "/sport-detail?id=" + sportId);
                    }
                } else {
                    if ("/sport".equals(path)) {
                        List<Sport> sportList = sportController.getAllData();
                        for (Sport sport : sportList) {
                            List<SportComment> comments = commentController.getCommentsBySportId(sport.getId());
                            request.setAttribute("comments_" + sport.getId(), comments);
                        }
                        request.setAttribute("sportList", sportList);
                        request.setAttribute("error_" + sportId, "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/sport.jsp").forward(request, response);
                    } else if ("/sport-detail".equals(path)) {
                        List<Sport> sportList = sportController.getSportById(sportId);
                        Sport sport = sportList.isEmpty() ? null : sportList.get(0);
                        List<SportComment> comments = commentController.getCommentsBySportId(sportId);
                        request.setAttribute("sport", sport);
                        request.setAttribute("comments", comments != null ? comments : List.of());
                        request.setAttribute("error", "Failed to post comment. Please try again.");
                        request.getRequestDispatcher("/user-side/sport-detail.jsp").forward(request, response);
                    }
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/sport?error=invalid_id");
            }
        } else {
            if ("/sport".equals(path)) {
                response.sendRedirect(request.getContextPath() + "/sport?error=invalid_input");
            } else {
                response.sendRedirect(request.getContextPath() + "/sport-detail?id=" + sportIdParam + "&error=invalid_input");
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}