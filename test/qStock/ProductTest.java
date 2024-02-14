package qStock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testProductInitialization() {
        // Create a new Product
        Product product = new Product("Laptop", "Electronics", 10, 1500.0);

        // Check the initialization of properties
        assertEquals("Laptop", product.getNumeProdus(), "Name should be 'Laptop'");
        assertEquals("Electronics", product.getCategorie(), "Category should be 'Electronics'");
        assertEquals(10, product.getCantitate(), "Quantity should be 10");
        assertEquals(1500.0, product.getPret(), 0.001, "Price should be 1500.0");
    }

    @Test
    public void testPropertyBindings() {
        // Create a new Product
        Product product = new Product("Laptop", "Electronics", 10, 1500.0);

        // Test property bindings
        assertEquals("Laptop", product.nameProperty().get(), "Name property should be bound to 'Laptop'");
        assertEquals("Electronics", product.categoryProperty().get(), "Category property should be bound to 'Electronics'");
        assertEquals(10, product.quantityProperty().get(), "Quantity property should be bound to 10");
        assertEquals(1500.0, product.priceProperty().get(), 0.001, "Price property should be bound to 1500.0");
    }
}
