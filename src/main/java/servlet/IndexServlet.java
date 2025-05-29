package servlet;

import java.io.IOException;
import java.util.List;
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

import model.Attraction;
import model.FoodItem;
import model.Movie;
import model.Music;
import model.Sport;
import utility.DatabaseConnection;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        // Ensure database connection is initialized if needed
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Fetch top 3 items for each category
        FoodItemControllerImplements foodController = new FoodItemControllerImplements();
        AttractionControllerImplements attractionController = new AttractionControllerImplements();
        MusicControllerImplements musicController = new MusicControllerImplements();
        MovieControllerImplements movieController = new MovieControllerImplements();
        SportControllerImplements sportsController = new SportControllerImplements();

        List<FoodItem> topFoods = foodController.getTopFoods(3);
        List<Attraction> topAttractions = attractionController.getTopAttractions(3);
        List<Music> topMusic = musicController.getTopMusic(3);
        List<Movie> topMovies = movieController.getTopMovies(3);
        List<Sport> topSports = sportsController.getTopSports(3);

        // Set attributes for JSP
        request.setAttribute("topFoods", topFoods);
        request.setAttribute("topAttractions", topAttractions);
        request.setAttribute("topMusic", topMusic);
        request.setAttribute("topMovies", topMovies);
        request.setAttribute("topSports", topSports);

        // Forward to JSP
        request.getRequestDispatcher("/user-side/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void destroy() {
        DatabaseConnection.closeConnection();
        super.destroy();
    }
}