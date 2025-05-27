package controller;

import java.util.List;
import model.Movie;

public interface MovieController {
    public boolean addMovie(Movie m);
    public List<Movie> getAllData();
    public boolean deleteMovie(int id);
    public List<Movie> getMovieById(int id);
    public boolean editMovie(Movie m);
}