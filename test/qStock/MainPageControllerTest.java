package qStock;

import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class MainPageControllerTest {

    @Test
    void createTables() {
        // Call the createTables method
        MainPageController.createTables();

        // Verify that the 'products' table is created
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "products", null);
            assertTrue(resultSet.next(), "Products table should exist");
        } catch (SQLException e) {
            fail("Exception thrown while checking if products table exists: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        // Load the JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            fail("Failed to load JDBC driver: " + e.getMessage());
        }

        // Establish connection to the database
        String url = "jdbc:mysql://localhost:3306/javafxapp";
        String username = "admin";
        String password = "1234";
        return DriverManager.getConnection(url, username, password);
    }
}
