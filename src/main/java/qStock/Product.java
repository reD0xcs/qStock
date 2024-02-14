package qStock;

import javafx.beans.property.*;

/**
 * Clasa Product reprezintă o entitate pentru stocarea informațiilor despre un produs.
 * Fiecare produs este caracterizat de un nume, o categorie, o cantitate și un preț.
 */
public class Product {
    private final StringProperty name;
    private final StringProperty category;
    private final IntegerProperty quantity;
    private final DoubleProperty price;

    /**
     * Constructorul clasei Product.
     * @param name Numele produsului.
     * @param category Categoria produsului.
     * @param quantity Cantitatea disponibilă a produsului.
     * @param price Prețul produsului.
     */
    public Product(String name, String category, int quantity, double price) {
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }

    /**
     * Metoda getter pentru obținerea proprietății name sub formă de StringProperty.
     * @return StringProperty pentru numele produsului.
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Metoda getter pentru obținerea proprietății category sub formă de StringProperty.
     * @return StringProperty pentru categoria produsului.
     */
    public StringProperty categoryProperty() {
        return category;
    }

    /**
     * Metoda getter pentru obținerea proprietății quantity sub formă de IntegerProperty.
     * @return IntegerProperty pentru cantitatea produsului.
     */
    public IntegerProperty quantityProperty() {
        return quantity;
    }

    /**
     * Metoda getter pentru obținerea proprietății price sub formă de DoubleProperty.
     * @return DoubleProperty pentru prețul produsului.
     */
    public DoubleProperty priceProperty() {
        return price;
    }

    /**
     * Metoda getter pentru obținerea numelui produsului sub formă de șir de caractere.
     * @return Numele produsului.
     */
    public String getNumeProdus() {
        return name.get();
    }

    /**
     * Metoda getter pentru obținerea categoriei produsului sub formă de șir de caractere.
     * @return Categoria produsului.
     */
    public String getCategorie() {
        return category.get();
    }

    /**
     * Metoda getter pentru obținerea cantității produsului sub formă de int.
     * @return Cantitatea produsului.
     */
    public int getCantitate() {
        return quantity.get();
    }

    /**
     * Metoda getter pentru obținerea prețului produsului sub formă de double.
     * @return Prețul produsului.
     */
    public double getPret() {
        return price.get();
    }
}
