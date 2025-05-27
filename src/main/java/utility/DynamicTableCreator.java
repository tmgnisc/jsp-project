package utility;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import model.FoodItem;
import model.User;
import org.apache.commons.codec.digest.DigestUtils;

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
                sql.append(sqlFieldName).append(" ").append(fieldType);
                // Add UNIQUE constraint for fields like email and username in User model
                if (modelClass == User.class && (fieldName.equals("email") || fieldName.equals("username"))) {
                    sql.append(" UNIQUE");
                }
                sql.append(", ");
            }
        }
        // Remove the trailing comma and space, then close the statement
        sql.setLength(sql.length() - 2);
        sql.append(")");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql.toString());
            System.out.println("Table " + tableName + " created successfully.");

            // Insert default admin user if the table is users
            if (modelClass == User.class) {
                insertDefaultAdminUser(conn);
            }
        } catch (SQLException e) {
            System.err.println("Error creating table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertDefaultAdminUser(Connection conn) {
        String defaultUsername = "admin";
        String defaultEmail = "admin@gmail.com";
        String defaultPassword = "Admin@123";
        String defaultFullName = "Admin User";
        String defaultRole = "admin";
        String defaultStatus = "active";
        String staticSalt = "NepalNavigatorSalt2025"; // Match with UsersServlet and LoginServlet

        // Hash the default password with salt
        String salt = defaultUsername + staticSalt;
        String saltedPassword = salt + defaultPassword;
        String hashedPassword = DigestUtils.shaHex(saltedPassword);

        String sql = "INSERT IGNORE INTO users (full_name, email, username, password, role, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, defaultFullName);
            pstmt.setString(2, defaultEmail);
            pstmt.setString(3, defaultUsername);
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, defaultRole);
            pstmt.setString(6, defaultStatus);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Default admin user inserted successfully.");
            } else {
                System.out.println("Default admin user already exists or insertion failed.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting default admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getSqlType(Class<?> javaType) {
        if (javaType == int.class || javaType == Integer.class) return "INT";
        if (javaType == String.class) return "VARCHAR(100)"; // Use VARCHAR with a reasonable length
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == float.class || javaType == Float.class) return "FLOAT";
        if (javaType == double.class || javaType == Double.class) return "DOUBLE";
        return "VARCHAR(100)"; // Default for other types, with a reasonable length
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
        return snakeCase.toString().substring(1); // Remove leading underscore
    }

    public static void main(String[] args) {
        // Test with FoodItem class
        createTableFromModel(FoodItem.class, "food_items");
        // Test with User class
        createTableFromModel(User.class, "users");
    }
}