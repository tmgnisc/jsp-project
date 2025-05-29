package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.FoodItem;
import utility.DatabaseConnection;

public class FoodItemControllerImplements implements FoodItemController {

    public FoodItemControllerImplements() {
    }

    private boolean ensureConnection() {
        Connection conn = DatabaseConnection.getConnection();
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addFoodItem(FoodItem f) {
        if (!ensureConnection()) {
            System.err.println("Cannot add food item: Database connection is not available.");
            return false;
        }

        String sql = "INSERT INTO food_items (name, description, category, image, ingredients, preparation_method, serving_suggestions, cultural_significance, region, tag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getName());
            pstmt.setString(2, f.getDescription());
            pstmt.setString(3, f.getCategory());
            pstmt.setString(4, f.getImage());
            pstmt.setString(5, f.getIngredients());
            pstmt.setString(6, f.getPreparationMethod());
            pstmt.setString(7, f.getServingSuggestions());
            pstmt.setString(8, f.getCulturalSignificance());
            pstmt.setString(9, f.getRegion());
            pstmt.setString(10, f.getTag());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding food item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<FoodItem> getAllData() {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve food items: Database connection is not available.");
            return new ArrayList<>();
        }

        List<FoodItem> foodItems = new ArrayList<>();
        String sql = "SELECT * FROM food_items";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                FoodItem item = new FoodItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("image"),
                    rs.getString("ingredients"),
                    rs.getString("preparation_method"),
                    rs.getString("serving_suggestions"),
                    rs.getString("cultural_significance"),
                    rs.getString("region"),
                    rs.getString("tag")
                );
                foodItems.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving food items: " + e.getMessage());
        }
        return foodItems;
    }

    @Override
    public boolean deleteFoodItem(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot delete food item: Database connection is not available.");
            return false;
        }

        String sql = "DELETE FROM food_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting food item with ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<FoodItem> getFoodItemById(int id) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve food item by ID: Database connection is not available.");
            return new ArrayList<>();
        }

        List<FoodItem> foodItems = new ArrayList<>();
        String sql = "SELECT * FROM food_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    FoodItem item = new FoodItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("image"),
                        rs.getString("ingredients"),
                        rs.getString("preparation_method"),
                        rs.getString("serving_suggestions"),
                        rs.getString("cultural_significance"),
                        rs.getString("region"),
                        rs.getString("tag")
                    );
                    foodItems.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving food item by ID: " + e.getMessage());
        }
        return foodItems;
    }

    @Override
    public boolean editFoodItem(FoodItem f) {
        if (!ensureConnection()) {
            System.err.println("Cannot edit food item: Database connection is not available.");
            return false;
        }

        String sql = "UPDATE food_items SET name = ?, description = ?, category = ?, image = ?, ingredients = ?, preparation_method = ?, serving_suggestions = ?, cultural_significance = ?, region = ?, tag = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getName());
            pstmt.setString(2, f.getDescription());
            pstmt.setString(3, f.getCategory());
            pstmt.setString(4, f.getImage());
            pstmt.setString(5, f.getIngredients());
            pstmt.setString(6, f.getPreparationMethod());
            pstmt.setString(7, f.getServingSuggestions());
            pstmt.setString(8, f.getCulturalSignificance());
            pstmt.setString(9, f.getRegion());
            pstmt.setString(10, f.getTag());
            pstmt.setInt(11, f.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error editing food item: " + e.getMessage());
            return false;
        }
    }

    public int getTotalFoods() {
        if (!ensureConnection()) {
            System.err.println("Cannot count food items: Database connection is not available.");
            return 0;
        }

        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM food_items";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting food items: " + e.getMessage());
        }
        return count;
    }

    public List<FoodItem> getTopFoods(int limit) {
        if (!ensureConnection()) {
            System.err.println("Cannot retrieve top foods: Database connection is not available.");
            return new ArrayList<>();
        }

        List<FoodItem> foods = new ArrayList<>();
        String sql = "SELECT * FROM food_items ORDER BY id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FoodItem food = new FoodItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getString("image"),
                        rs.getString("ingredients"),
                        rs.getString("preparation_method"),
                        rs.getString("serving_suggestions"),
                        rs.getString("cultural_significance"),
                        rs.getString("region"),
                        rs.getString("tag")
                    );
                    foods.add(food);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top foods: " + e.getMessage());
        }
        return foods;
    }
}