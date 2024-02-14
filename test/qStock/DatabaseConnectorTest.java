package qStock;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectorTest {
    private static final String TEST_DB_URL = "jdbc:h2:mem:test";
    private static final String TEST_DB_USER = "admin";
    private static final String TEST_DB_PASSWORD = "1234";

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testConnect() {
        assertNotNull(DatabaseConnector.connect(), "Connection should not be null");
    }

    @Test
    void testCreateTables() {
        // Create tables
        DatabaseConnector.createTables();

        // Check if users table exists
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_name = 'USERS'");
            assertTrue(resultSet.next(), "Failed to execute query");
            int count = resultSet.getInt(1);
            assertEquals(1, count, "Users table should exist");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Exception thrown while checking if users table exists");
        }
    }
}
