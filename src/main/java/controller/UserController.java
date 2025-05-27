package controller;

import java.util.List;
import model.User;

public interface UserController {
    public boolean addUser(User u);
    public List<User> getAllData();
    public boolean deleteUser(int id);
    public List<User> getUserById(int id);
    public boolean editUser(User u);
}