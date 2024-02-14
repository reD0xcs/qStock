package qStock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clasa LoginController este responsabilă pentru gestionarea interacțiunilor din ecranul de autentificare.
 * Verifică credențialele utilizatorului și direcționează către ecranul principal (MainPage) în caz de autentificare reușită.
 * Această clasă este asociată cu fișierul FXML "loginPage.fxml".
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Stage primaryStage;
    QStockApp app;

    /**
     * Setează stadiul principal al aplicației.
     *
     * @param primaryStage Stadiul principal al aplicației.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Setează instanța clasei QStockApp pentru a accesa metodele sale.
     *
     * @param app Instanța clasei QStockApp.
     */
    public void setApp(QStockApp app) {
        this.app = app;
    }

    /**
     * Gestionarea acțiunii de apăsare a butonului de autentificare.
     *
     * @param event Evenimentul generat de apăsarea butonului.
     */
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (checkCredentials(username, password)) {
            // Autentificare reușită, încarcă MainPage
            FXMLLoader mainPageLoader = new FXMLLoader(getClass().getResource("/mainMenuV2.fxml"));
            Parent mainPageRoot;
            try {
                mainPageRoot = mainPageLoader.load();

                // Configurează controllerul MainPage
                MainPageController mainPageController = mainPageLoader.getController();

                // Obține primaryStage folosind getHostServices
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Setează scena doar dacă primaryStage nu este null
                if (primaryStage != null) {
                    primaryStage.setScene(new Scene(mainPageRoot));
                } else {
                    showErrorAlert("Eroare: primaryStage este null.");
                }
            } catch (IOException e) {
                // Loghează excepția în scopuri de diagnosticare
                e.printStackTrace();
                // Afișează un mesaj de eroare
                showErrorAlert("Eroare la încărcarea MainPage. Vă rugăm să încercați din nou. Excepție: " + e.getMessage());
            }
        } else {
            // Autentificare eșuată, afișează o fereastră de eroare
            showErrorAlert("Nume de utilizator sau parolă incorecte");
        }
    }

    /**
     * Verifică dacă credențialele introduse de utilizator sunt corecte.
     *
     * @param username Numele de utilizator introdus.
     * @param password Parola introdusă.
     * @return true dacă credențialele sunt valide, false în caz contrar.
     */
    boolean checkCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returnează true dacă credențialele sunt valide
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Afișează o fereastră de alertă pentru erori.
     *
     * @param message Mesajul de eroare de afișat.
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare la Autentificare");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
