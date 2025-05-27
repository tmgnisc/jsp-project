package controller;

import java.util.List;
import model.Attraction;

public interface AttractionController {
    public boolean addAttraction(Attraction a);
    public List<Attraction> getAllData();
    public boolean deleteAttraction(int id);
    public List<Attraction> getAttractionById(int id);
    public boolean editAttraction(Attraction a);
}