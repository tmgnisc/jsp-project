package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.AttractionControllerImplements;
import controller.FoodItemControllerImplements;
import controller.MovieControllerImplements;
import controller.MusicControllerImplements;
import controller.SportControllerImplements;
import controller.UserControllerImplements;
import utility.DatabaseConnection;

/**
 * Servlet implementation class DashboardServlet
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object user = request.getSession().getAttribute("user");
        if (user == null) {
            request.getSession().setAttribute("notify", "Please log in to access this page.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if user is an admin
        if (!(user instanceof model.User) || !"admin".equalsIgnoreCase(((model.User) user).getRole())) {
            request.getSession().setAttribute("notify", "Access denied. Admins only.");
            response.sendRedirect(request.getContextPath() + "/user-side/index.jsp");
            return;
        }

        // Fetch counts from controllers
        UserControllerImplements userController = new UserControllerImplements();
        FoodItemControllerImplements foodController = new FoodItemControllerImplements();
        AttractionControllerImplements attractionController = new AttractionControllerImplements();
        MusicControllerImplements musicController = new MusicControllerImplements();
        MovieControllerImplements movieController = new MovieControllerImplements();
        SportControllerImplements sportsController = new SportControllerImplements();

        int totalUsers = userController.getTotalUsers();
        int totalFoods = foodController.getTotalFoods();
        int totalAttractions = attractionController.getTotalAttractions();
        int totalMusic = musicController.getTotalMusic();
        int totalMovies = movieController.getTotalMovies();
        int totalSports = sportsController.getTotalSports();

        // Set attributes for JSP
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalFoods", totalFoods);
        request.setAttribute("totalAttractions", totalAttractions);
        request.setAttribute("totalMusic", totalMusic);
        request.setAttribute("totalMovies", totalMovies);
        request.setAttribute("totalSports", totalSports);
        request.setAttribute("notify", request.getSession().getAttribute("notify") != null ? request.getSession().getAttribute("notify") : "");
        request.getSession().removeAttribute("notify");
	
		request.getRequestDispatcher("/admin-side/dashboard.jsp").forward(request, response);
	}

	@Override
    public void destroy() {
        DatabaseConnection.closeConnection();
        super.destroy();
    }

}
