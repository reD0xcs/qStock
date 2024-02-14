package qStock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clasa QStockApp este clasa principală pentru aplicația QStock.
 * Aceasta extinde clasa Application din JavaFX și este responsabilă pentru inițializarea
 * aplicației, crearea tabelelor necesare în baza de date și încărcarea ecranului de autentificare.
 *
 * @author Diaconu Andrei Catalin
 */
public class QStockApp extends Application {

    /**
     * Metoda principală care lansează aplicația JavaFX.
     *
     * @param args Argumentele liniei de comandă furnizate aplicației.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Metoda start este apelată atunci când aplicația JavaFX este pornită.
     * Inițializează tabelele necesare în baza de date și încarcă ecranul de autentificare.
     *
     * @param primaryStage Stadiul principal al aplicației.
     * @throws Exception Dacă apare o eroare în timpul pornirii aplicației.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creează tabelele necesare
        DatabaseConnector.createTables();
        MainPageController.createTables();

        // Încarcă ecranul de autentificare
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/loginPage.fxml"));
        Parent loginRoot = loginLoader.load();
        primaryStage.setScene(new Scene(loginRoot));
        primaryStage.show();

        // Configurează controllerul de autentificare
        LoginController loginController = loginLoader.getController();
        loginController.setPrimaryStage(primaryStage);
        loginController.setApp(this);
    }
}
