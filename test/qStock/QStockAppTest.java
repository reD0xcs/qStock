package qStock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QStockAppTest {

    @Test
    void testMainMethod() {
        // Simply call the main method to check if it runs without throwing any exceptions
        assertDoesNotThrow(() -> QStockApp.main(new String[]{}));
    }
}