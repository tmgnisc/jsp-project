package utility;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import model.FoodItem;


public class DynamicTableCreator {
    public static void createTableFromModel(Class<?> modelClass, String tableName) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        
        // Get all fields from the model class
        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String sqlFieldName = camelCaseToSnakeCase(fieldName); // Convert camelCase to snake_case
            String fieldType = getSqlType(field.getType());

            if (fieldName.equals("id")) {
                sql.append(sqlFieldName).append(" INT AUTO_INCREMENT PRIMARY KEY, ");
            } else {
                sql.append(sqlFieldName).append(" ").append(fieldType).append(", ");
            }
        }
        // Remove the trailing comma and space, then close the statement
        sql.setLength(sql.length() - 2);
        sql.append(")");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql.toString());
            System.out.println("Table " + tableName + " created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getSqlType(Class<?> javaType) {
        if (javaType == int.class || javaType == Integer.class) return "INT";
        if (javaType == String.class) return "TEXT"; // Use TEXT for all String fields for consistency
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        return "TEXT"; // Default for other types
    }

    private static String camelCaseToSnakeCase(String camelCase) {
        StringBuilder snakeCase = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                snakeCase.append('_').append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }
        return snakeCase.toString();
    }

    public static void main(String[] args) {
        createTableFromModel(FoodItem.class, "food_items");
    }
}