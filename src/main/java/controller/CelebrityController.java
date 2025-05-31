// In CelebrityController.java
package controller;

import java.util.List;
import model.Celebrity;

public interface CelebrityController {
    boolean addCelebrity(Celebrity c);
    List<Celebrity> getAllData();
    boolean deleteCelebrity(int id);
    List<Celebrity> getCelebrityById(int id);
    boolean editCelebrity(Celebrity c);
    List<Celebrity> getCelebritiesByIds(List<Integer> ids); // New method
}