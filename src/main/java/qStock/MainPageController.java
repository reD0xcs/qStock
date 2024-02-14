package qStock;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clasa MainPageController controlează logica și interacțiunea pentru ecranul principal al aplicației QStock.
 * Această clasă gestionează acțiunile utilizatorului și actualizările interfeței pentru afișarea și manipularea datelor produselor.
 */
public class MainPageController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Text productNameText;

    @FXML
    private Text categoryText;

    @FXML
    private Text quantityText;

    @FXML
    private Text priceText;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    /**
     * Metoda handleLogout este apelată când utilizatorul dorește să se deconecteze.
     * Aceasta încarcă pagina de autentificare și închide fereastra curentă.
     */
    @FXML
    private void handleLogout() {
        try {
            // Încarcă pagina de autentificare din fișierul FXML
            InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream("loginPage.fxml");
            Parent root = new FXMLLoader().load(fxmlStream);

            // Creează o nouă scenă pentru pagina de autentificare
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));

            // Afișează pagina de autentificare
            loginStage.show();

            // Închide fereastra curentă (fereastra principală a aplicației)
            Stage currentStage = (Stage) mainTabPane.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda initialize este apelată automat la încărcarea ecranului principal.
     * Aceasta setează comportamentul inițial al interfeței și populează tabelul de produse.
     */
    @FXML
    private void initialize() {
        // Inițializează coloanele cu valorile lor corespunzătoare
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty().asObject());

        // // Setează un custom cell factory personalizat pentru coloana quantityColumn
        quantityColumn.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);

                if (empty || quantity == null) {
                    setText(null);
                    setStyle(""); // Resetează stilul celulei pentru celulele goale
                } else {
                    setText(quantity.toString());

                    // Setează un fundal roșu pentru celulele cu cantitate mai mică de 100
                    if (quantity < 100) {
                        setStyle("-fx-background-color: #fc4e03CC;");
                    } else {
                        setStyle(""); // Resetează stilul celulei pentru celulele cu cantitate >= 100
                    }
                }
            }

        });
        // Adaugă clasa de stil pentru tab-ul "Log Out"
        for (Tab tab : mainTabPane.getTabs()) {
            if ("Log Out".equals(tab.getText())) {
                tab.getStyleClass().add("logout-tab");
                break;
            }
        }
        populateProductTable();
    }

    /**
     * Metoda handleAdaugareProdusSubmit se ocupă de acțiunea de adăugare a unui produs și reacționează
     * la evenimentul declanșat de apăsarea butonului corespunzător în interfața grafică.
     * Extrage valorile introduse în câmpurile de text pentru nume, categorie, cantitate și preț, apoi
     * validează și adaugă produsul în baza de date. Afisează alerte de succes sau eroare în funcție
     * de rezultatul operației. De asemenea, actualizează tabela de produse.
     */
    @FXML
    private void handleAdaugareProdusSubmit() {
        TextField numeProdusAdaugareField = (TextField) mainTabPane.lookup("#numeProdusAdaugareField");
        TextField categorieField = (TextField) mainTabPane.lookup("#categorieField");
        TextField cantitateField = (TextField) mainTabPane.lookup("#cantitateField");
        TextField pretField = (TextField) mainTabPane.lookup("#pretField");

        String numeProdusAdaugare = numeProdusAdaugareField.getText();
        String categorie = categorieField.getText();
        String cantitate = cantitateField.getText();
        String pret = pretField.getText();

        if (!checkIfProductExists(numeProdusAdaugare)) {
            if (addProductToDatabase(numeProdusAdaugare, categorie, Integer.parseInt(cantitate), Double.parseDouble(pret))) {
                showSuccessAlert("Produs adăugat cu succes");
            } else {
                showErrorAlert("Eroare la adăugarea produsului");
            }
        } else {
            showErrorAlert("Produsul pe care doriți să-l adăugați există deja");
        }
        populateProductTable();
    }

    /**
     * Metoda handleStergereProdusSubmit se ocupă de acțiunea de ștergere a unui produs și reacționează
     * la evenimentul declanșat de apăsarea butonului corespunzător în interfața grafică.
     * Extrage valorile introduse în câmpurile de text pentru nume produs și confirmare nume, apoi
     * validează și șterge produsul din baza de date. Afisează alerte de succes sau eroare în funcție
     * de rezultatul operației. De asemenea, actualizează tabela de produse.
     */
    @FXML
    private void handleStergereProdusSubmit() {
        TextField numeProdusStergereField = (TextField) mainTabPane.lookup("#numeProdusStergereField");
        TextField confirmareNumeField = (TextField) mainTabPane.lookup("#confirmareNumeField");

        String numeProdusStergere = numeProdusStergereField.getText();
        String confirmareNume = confirmareNumeField.getText();

        if (numeProdusStergere.equals(confirmareNume)) {
            boolean productExists = checkIfProductExists(numeProdusStergere);

            if (productExists) {
                if (deleteProductFromDatabase(numeProdusStergere)) {
                    showSuccessAlert("Ștergere realizată cu succes");
                } else {
                    showErrorAlert("Eroare la ștergerea produsului");
                }
            } else {
                showErrorAlert("Produsul pe care doriți să îl ștergeți nu există");
            }
        } else {
            showErrorAlert("Nume produs și confirmare nume diferă");
        }
        populateProductTable();
    }

    /**
     * Metoda handleModificareInventarSubmit se ocupă de acțiunea de modificare a cantității unui produs
     * și reacționează la evenimentul declanșat de apăsarea butonului corespunzător în interfața grafică.
     * Extrage valorile introduse în câmpurile de text pentru nume produs și cantitate nouă, apoi
     * validează și actualizează cantitatea produsului în baza de date. Afisează alerte de succes sau eroare
     * în funcție de rezultatul operației. De asemenea, actualizează tabela de produse.
     */
    @FXML
    private void handleModificareInventarSubmit() {
        TextField numeProdusModificareField = (TextField) mainTabPane.lookup("#numeProdusModificareField");
        TextField cantitateNouaField = (TextField) mainTabPane.lookup("#cantitateNouaField");

        String numeProdusModificare = numeProdusModificareField.getText();
        String cantitateNoua = cantitateNouaField.getText();

        if (checkIfProductExists(numeProdusModificare)) {
            if (modifyProductQuantity(numeProdusModificare, Integer.parseInt(cantitateNoua))) {
                showSuccessAlert("Modificare realizată cu succes");
            } else {
                showErrorAlert("Eroare la modificarea cantității produsului");
            }
        } else {
            showErrorAlert("Produsul cu numele specificat nu există");
        }
        populateProductTable();
    }

    /**
     * Metoda handleCautareProdusSubmit se ocupă de acțiunea de căutare a unui produs după nume
     * și reacționează la evenimentul declanșat de apăsarea butonului corespunzător în interfața grafică.
     * Extrage valoarea introdusă în câmpul de text pentru nume produs, apoi validează și afișează detaliile
     * produsului găsit sau un mesaj de eroare în funcție de rezultatul operației.
     */
    @FXML
    private void handleCautareProdusSubmit() {
        TextField numeProdusCautareField = (TextField) mainTabPane.lookup("#numeProdusCautareField");

        String numeProdusCautare = numeProdusCautareField.getText();

        if (!numeProdusCautare.isEmpty()) {
            Product product = searchProductByName(numeProdusCautare);

            if (product != null) {
                displayProductDetails(product);
            } else {
                showErrorAlert("Produs inexistent");
            }
        }
    }

    private Product searchProductByName(String numeProdus) {
        String query = "SELECT * FROM products WHERE numeProdus = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, numeProdus);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Product found, create and return a Product object
                    return new Product(
                            resultSet.getString("numeProdus"),
                            resultSet.getString("categorie"),
                            resultSet.getInt("cantitate"),
                            resultSet.getDouble("pret")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Product not found
    }

    /**
     * Metoda populateProductTable încarcă datele produselor din baza de date și le afișează în tabelul corespunzător.
     */
    private void populateProductTable() {
        productList.clear(); // Șterge datele existente

        String query = "SELECT * FROM products";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("numeProdus");
                String category = resultSet.getString("categorie");
                int quantity = resultSet.getInt("cantitate");
                double price = resultSet.getDouble("pret");

                productList.add(new Product(name, category, quantity, price));
            }

            // Setează elementele în TableView
            productTableView.setItems(productList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda displayProductDetails afișează detaliile produsului selectat în interfață.
     *
     * @param product Produsul selectat
     */
    private void displayProductDetails(Product product) {
        // Setează textul elementelor Text cu detaliile produsului
        productNameText.setText("Nume Produs: " + product.getNumeProdus());
        categoryText.setText("Categorie: " + product.getCategorie());
        quantityText.setText("Cantitate: " + product.getCantitate());
        priceText.setText("Preț: " + product.getPret());
    }

    /**
     * Metoda modifyProductQuantity actualizează cantitatea unui produs în baza de date.
     *
     * @param numeProdus   Numele produsului
     * @param cantitateNoua Cantitatea nouă
     * @return true dacă actualizarea este reușită, false în caz contrar
     */
    private boolean modifyProductQuantity(String numeProdus, int cantitateNoua) {
        String query = "UPDATE products SET cantitate = ? WHERE numeProdus = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cantitateNoua);
            preparedStatement.setString(2, numeProdus);

            return preparedStatement.executeUpdate() > 0; // Returnează true dacă actualizarea este reușită
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Metoda checkIfProductExists verifică dacă un produs există în baza de date.
     *
     * @param numeProdus Numele produsului
     * @return true dacă produsul există, false în caz contrar
     */
    private boolean checkIfProductExists(String numeProdus) {
        String query = "SELECT * FROM products WHERE numeProdus = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, numeProdus);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returnează true dacă produsul există
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Metoda addProductToDatabase adaugă un produs în baza de date.
     *
     * @param numeProdus Numele produsului
     * @param categorie  Categoria produsului
     * @param cantitate  Cantitatea produsului
     * @param pret       Prețul produsului
     * @return true dacă adăugarea este reușită, false în caz contrar
     */
    private boolean addProductToDatabase(String numeProdus, String categorie, int cantitate, double pret) {
        createTables(); // Asigură că tabela 'products' există

        String query = "INSERT INTO products (numeProdus, categorie, cantitate, pret) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, numeProdus);
            preparedStatement.setString(2, categorie);
            preparedStatement.setInt(3, cantitate);
            preparedStatement.setDouble(4, pret);

            return preparedStatement.executeUpdate() > 0; // Returnează true dacă inserția este reușită
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Metoda deleteProductFromDatabase șterge un produs din baza de date.
     *
     * @param numeProdus Numele produsului
     * @return true dacă ștergerea este reușită, false în caz contrar
     */
    private boolean deleteProductFromDatabase(String numeProdus) {
        String query = "DELETE FROM products WHERE numeProdus = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, numeProdus);

            return preparedStatement.executeUpdate() > 0; // Returnează true dacă ștergerea este reușită
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Metoda getConnection furnizează o conexiune la baza de date.
     *
     * @return Conexiunea la baza de date
     * @throws SQLException Excepție SQL
     */
    private static Connection getConnection() throws SQLException {
        // Încarcă driverul JDBC
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://localhost:3306/javafxapp";
        String username = "admin";
        String password = "1234";

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Metoda createTables asigură că tabela 'products' există în baza de date.
     */
    public static void createTables() {
        String createProductsTableQuery = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "numeProdus VARCHAR(255) NOT NULL," +
                "categorie VARCHAR(255)," +
                "cantitate INT," +
                "pret DOUBLE" +
                ")";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createProductsTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda showSuccessAlert afișează o alertă de succes.
     *
     * @param message Mesajul de afișat în alertă
     */
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Metoda showErrorAlert afișează o alertă de eroare.
     *
     * @param message Mesajul de afișat în alertă
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
