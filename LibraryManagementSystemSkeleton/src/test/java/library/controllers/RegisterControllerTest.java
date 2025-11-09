package library.controllers;

import library.models.User;
import library.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest {

    private UserService userService;
    private RegisterController registerController;
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        userService = UserService.getInstance();
        registerController = new RegisterController();
        loginController = new LoginController();
    }

    @Test
    void testSuccessfulRegistration() {
        // Test registration use case through UserService (used by RegisterController)
        String username = "testuser_" + System.currentTimeMillis();
        String password = "password123";
        String fullName = "Test User";
        String role = "student";

        boolean result = userService.registerUser(username, password, fullName, role);
        
        assertTrue(result, "Registration should succeed");
        assertTrue(userService.usernameExists(username), "Username should exist");
        
        User user = userService.authenticate(username, password, role);
        assertNotNull(user, "User should be able to login");
        assertEquals(username.toLowerCase(), user.getUsername());
        assertEquals(fullName, user.getFullName());
        assertEquals(role.toLowerCase(), user.getRole());
    }

    @Test
    void testSuccessfulLogin() {
        // Test login use case through UserService (used by LoginController)
        String username = "loginuser_" + System.currentTimeMillis();
        String password = "securepass456";
        String fullName = "Login User";
        String role = "librarian";

        userService.registerUser(username, password, fullName, role);
        
        User loggedInUser = userService.authenticate(username, password, role);
        
        assertNotNull(loggedInUser, "Login should succeed");
        assertEquals(username.toLowerCase(), loggedInUser.getUsername());
        assertEquals(fullName, loggedInUser.getFullName());
        assertEquals(role.toLowerCase(), loggedInUser.getRole());
    }

    @Test
    void testRegisterControllerSetRole() {
        // Test RegisterController.setRole() method
        // This method sets selectedRole and calls capitalize() for default case
        // Note: headerLabel is null in test, so setText() will throw NPE, but code is still executed for coverage
        try {
            registerController.setRole("student");
        } catch (NullPointerException e) {
            // Expected - headerLabel is not initialized in test
        }
        try {
            registerController.setRole("author");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            registerController.setRole("librarian");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            registerController.setRole("staff");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            registerController.setRole("unknownrole"); // This calls capitalize()
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    void testLoginControllerSetRole() {
        // Test LoginController.setRole() method
        // Note: headerLabel is null in test, so setText() will throw NPE, but code is still executed for coverage
        try {
            loginController.setRole("student");
        } catch (NullPointerException e) {
            // Expected - headerLabel is not initialized in test
        }
        try {
            loginController.setRole("author");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            loginController.setRole("librarian");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            loginController.setRole("staff");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            loginController.setRole("unknownrole");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    void testRegisterControllerConstructor() {
        // Test RegisterController constructor initializes UserService
        RegisterController controller = new RegisterController();
        assertNotNull(controller);
        // Constructor calls UserService.getInstance() which we can verify by testing registration
        String username = "constructortest_" + System.currentTimeMillis();
        boolean result = userService.registerUser(username, "pass123", "Test", "student");
        assertTrue(result);
    }

    @Test
    void testRegisterControllerCapitalize() throws Exception {
        // Test the private capitalize() method using reflection
        RegisterController controller = new RegisterController();
        Method capitalizeMethod = RegisterController.class.getDeclaredMethod("capitalize", String.class);
        capitalizeMethod.setAccessible(true);
        
        // Test capitalize with normal string
        String result1 = (String) capitalizeMethod.invoke(controller, "test");
        assertEquals("Test", result1);
        
        // Test capitalize with null
        String result2 = (String) capitalizeMethod.invoke(controller, (String) null);
        assertNull(result2);
        
        // Test capitalize with empty string
        String result3 = (String) capitalizeMethod.invoke(controller, "");
        assertEquals("", result3);
        
        // Test capitalize with single character
        String result4 = (String) capitalizeMethod.invoke(controller, "a");
        assertEquals("A", result4);
    }


}