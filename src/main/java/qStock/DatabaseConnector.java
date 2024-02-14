package qStock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clasa DatabaseConnector oferă funcționalități pentru conectarea și interacțiunea cu baza de date MySQL.
 * Această clasă include metode pentru crearea tabelelor necesare și adăugarea unor utilizatori inițiali în baza de date.
 */
public class DatabaseConnector {

    /**
     * Metoda connect realizează conexiunea la baza de date MySQL.
     *
     * @return Conexiunea la baza de date sau null în caz de eșec.
     */
    public static Connection connect() {
        try {
            // Încarcă driverul JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Conectează-te la baza de date
            String url = "jdbc:mysql://localhost:3306/javafxapp";
            String username = "admin";
            String password = "1234";

            return DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metoda createTables crează tabelele necesare pentru aplicație și adaugă utilizatori inițiali.
     */
    public static void createTables() {
        createUsersTable();
        addInitialUsers();
    }

    /**
     * Metoda createUsersTable creează tabela "users" dacă nu există.
     */
    private static void createUsersTable() {
        String createUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL" +
                ")";
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createUsersTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda addInitialUsers adaugă utilizatori inițiali în tabela "users".
     */
    private static void addInitialUsers() {
        String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery)) {
            // Adaugă utilizatorul admin
            preparedStatement.setString(1, "admin");
            preparedStatement.setString(2, "adminpass");
            preparedStatement.executeUpdate();

            // Adaugă utilizatorul user1
            preparedStatement.setString(1, "user1");
            preparedStatement.setString(2, "userpass");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda main este utilizată pentru testarea conexiunii și crearea tabelelor.
     *
     * @param args Argumentele liniei de comandă (nu sunt utilizate în acest context).
     */
    public static void main(String[] args) {
        // Testează conexiunea
        Connection connection = connect();
        if (connection != null) {
            System.out.println("Conectat la baza de date MySQL!");
            createTables();
        } else {
            System.err.println("Eșec la conectarea la baza de date MySQL!");
        }
    }
}
