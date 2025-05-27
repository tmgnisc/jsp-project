package controller;

import java.util.List;
import model.Music;

public interface MusicController {
    public boolean addMusic(Music m);
    public List<Music> getAllData();
    public boolean deleteMusic(int id);
    public List<Music> getMusicById(int id);
    public boolean editMusic(Music m);
}