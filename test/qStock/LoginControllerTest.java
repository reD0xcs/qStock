package qStock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @Test
    void checkCredentials() {
        // Create an instance of LoginController
        LoginController loginController = new LoginController();

        // Test valid credentials
        assertTrue(loginController.checkCredentials("admin", "adminpass"), "Valid credentials should return true");

        // Test invalid credentials
        assertFalse(loginController.checkCredentials("invalidUsername", "invalidPassword"), "Invalid credentials should return false");
    }
}
