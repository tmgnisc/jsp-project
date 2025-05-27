package controller;

import java.util.List;
import model.Sport;

public interface SportController {
    public boolean addSport(Sport s);
    public List<Sport> getAllData();
    public boolean deleteSport(int id);
    public List<Sport> getSportById(int id);
    public boolean editSport(Sport s);
}