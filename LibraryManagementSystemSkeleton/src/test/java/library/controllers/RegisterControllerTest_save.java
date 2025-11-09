package library.controllers;

import library.models.User;
import library.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest_save {

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Get the UserService instance
        userService = UserService.getInstance();
    }

    @Test
    void testSuccessfulRegistration() {
        // Arrange
        String username = "testuser_" + System.currentTimeMillis(); // Unique username
        String password = "password123";
        String fullName = "Test User";
        String role = "student";

        // Act - Register the user
        boolean registrationResult = userService.registerUser(username, password, fullName, role);

        // Assert
        assertTrue(registrationResult, "Registration should be successful");
        assertTrue(userService.usernameExists(username), "Username should exist after registration");
        
        // Verify the user can be authenticated
        User authenticatedUser = userService.authenticate(username, password, role);
        assertNotNull(authenticatedUser, "User should be able to authenticate after registration");
        assertEquals(username.toLowerCase(), authenticatedUser.getUsername(), "Username should match");
        assertEquals(fullName, authenticatedUser.getFullName(), "Full name should match");
        assertEquals(role.toLowerCase(), authenticatedUser.getRole(), "Role should match");
    }

    @Test
    void testSuccessfulLogin() {
        // Arrange - First register a user
        String username = "loginuser_" + System.currentTimeMillis(); // Unique username
        String password = "securepass456";
        String fullName = "Login Test User";
        String role = "librarian";

        // Register the user first
        boolean registered = userService.registerUser(username, password, fullName, role);
        assertTrue(registered, "User should be registered successfully");

        // Act - Attempt to login
        User loggedInUser = userService.authenticate(username, password, role);

        // Assert
        assertNotNull(loggedInUser, "Login should be successful with correct credentials");
        assertEquals(username.toLowerCase(), loggedInUser.getUsername(), "Username should match");
        assertEquals(fullName, loggedInUser.getFullName(), "Full name should match");
        assertEquals(role.toLowerCase(), loggedInUser.getRole(), "Role should match");
    }

    @Test
    void testRegistrationWithDuplicateUsername() {
        // Arrange
        String username = "duplicate_" + System.currentTimeMillis();
        String password = "password123";
        String fullName = "First User";
        String role = "student";

        // Register first user
        boolean firstRegistration = userService.registerUser(username, password, fullName, role);
        assertTrue(firstRegistration, "First registration should succeed");

        // Act - Try to register with same username
        boolean duplicateRegistration = userService.registerUser(username, "differentpass", "Different User", "author");

        // Assert
        assertFalse(duplicateRegistration, "Registration with duplicate username should fail");
        assertTrue(userService.usernameExists(username), "Username should still exist");
    }

    @Test
    void testRegistrationWithNullUsername() {
        // Act
        boolean result = userService.registerUser(null, "password123", "Test User", "student");

        // Assert
        assertFalse(result, "Registration with null username should fail");
    }

    @Test
    void testRegistrationWithEmptyUsername() {
        // Act
        boolean result1 = userService.registerUser("", "password123", "Test User", "student");
        boolean result2 = userService.registerUser("   ", "password123", "Test User", "student");

        // Assert
        assertFalse(result1, "Registration with empty username should fail");
        assertFalse(result2, "Registration with whitespace-only username should fail");
    }

    @Test
    void testRegistrationWithNullPassword() {
        // Act
        boolean result = userService.registerUser("testuser", null, "Test User", "student");

        // Assert
        assertFalse(result, "Registration with null password should fail");
    }

    @Test
    void testRegistrationWithEmptyPassword() {
        // Act
        boolean result1 = userService.registerUser("testuser", "", "Test User", "student");
        boolean result2 = userService.registerUser("testuser", "   ", "Test User", "student");

        // Assert
        assertFalse(result1, "Registration with empty password should fail");
        assertFalse(result2, "Registration with whitespace-only password should fail");
    }

    @Test
    void testRegistrationWithNullFullName() {
        // Act
        boolean result = userService.registerUser("testuser", "password123", null, "student");

        // Assert
        assertFalse(result, "Registration with null full name should fail");
    }

    @Test
    void testRegistrationWithEmptyFullName() {
        // Act
        boolean result1 = userService.registerUser("testuser", "password123", "", "student");
        boolean result2 = userService.registerUser("testuser", "password123", "   ", "student");

        // Assert
        assertFalse(result1, "Registration with empty full name should fail");
        assertFalse(result2, "Registration with whitespace-only full name should fail");
    }

    @Test
    void testLoginWithWrongPassword() {
        // Arrange - Register a user
        String username = "wrongpass_" + System.currentTimeMillis();
        String correctPassword = "correctpass123";
        String wrongPassword = "wrongpass456";
        String fullName = "Test User";
        String role = "student";

        userService.registerUser(username, correctPassword, fullName, role);

        // Act - Try to login with wrong password
        User result = userService.authenticate(username, wrongPassword, role);

        // Assert
        assertNull(result, "Login with wrong password should fail");
    }

    @Test
    void testLoginWithWrongRole() {
        // Arrange - Register a user as student
        String username = "wrongrole_" + System.currentTimeMillis();
        String password = "password123";
        String fullName = "Test User";
        String correctRole = "student";
        String wrongRole = "librarian";

        userService.registerUser(username, password, fullName, correctRole);

        // Act - Try to login with wrong role
        User result = userService.authenticate(username, password, wrongRole);

        // Assert
        assertNull(result, "Login with wrong role should fail");
    }

    @Test
    void testLoginWithNonExistentUser() {
        // Act - Try to login with non-existent username
        User result = userService.authenticate("nonexistentuser", "password123", "student");

        // Assert
        assertNull(result, "Login with non-existent user should fail");
    }

    @Test
    void testLoginWithNullUsername() {
        // Act
        User result = userService.authenticate(null, "password123", "student");

        // Assert
        assertNull(result, "Login with null username should fail");
    }

    @Test
    void testLoginWithNullPassword() {
        // Arrange - Register a user
        String username = "nullpass_" + System.currentTimeMillis();
        userService.registerUser(username, "password123", "Test User", "student");

        // Act
        User result = userService.authenticate(username, null, "student");

        // Assert
        assertNull(result, "Login with null password should fail");
    }

    @Test
    void testRegistrationWithDifferentRoles() {
        // Test registration with different roles
        String baseUsername = "roleuser_" + System.currentTimeMillis();
        
        // Test student role
        boolean studentResult = userService.registerUser(baseUsername + "_student", "pass123", "Student User", "student");
        assertTrue(studentResult, "Student registration should succeed");
        
        // Test author role
        boolean authorResult = userService.registerUser(baseUsername + "_author", "pass123", "Author User", "author");
        assertTrue(authorResult, "Author registration should succeed");
        
        // Test librarian role
        boolean librarianResult = userService.registerUser(baseUsername + "_librarian", "pass123", "Librarian User", "librarian");
        assertTrue(librarianResult, "Librarian registration should succeed");
        
        // Test staff role (should be treated as student)
        boolean staffResult = userService.registerUser(baseUsername + "_staff", "pass123", "Staff User", "staff");
        assertTrue(staffResult, "Staff registration should succeed");
    }

    @Test
    void testLoginWithDifferentRoles() {
        // Arrange - Register users with different roles
        String studentUser = "studentlogin_" + System.currentTimeMillis();
        String authorUser = "authorlogin_" + System.currentTimeMillis();
        String librarianUser = "librarianlogin_" + System.currentTimeMillis();

        userService.registerUser(studentUser, "pass123", "Student", "student");
        userService.registerUser(authorUser, "pass123", "Author", "author");
        userService.registerUser(librarianUser, "pass123", "Librarian", "librarian");

        // Act & Assert - Login with each role
        User studentLogin = userService.authenticate(studentUser, "pass123", "student");
        assertNotNull(studentLogin, "Student login should succeed");

        User authorLogin = userService.authenticate(authorUser, "pass123", "author");
        assertNotNull(authorLogin, "Author login should succeed");

        User librarianLogin = userService.authenticate(librarianUser, "pass123", "librarian");
        assertNotNull(librarianLogin, "Librarian login should succeed");
    }

    @Test
    void testUsernameCaseInsensitivity() {
        // Arrange
        String username = "CaseTest_" + System.currentTimeMillis();
        String password = "pass123";
        String fullName = "Case Test User";
        String role = "student";

        // Register with lowercase
        userService.registerUser(username.toLowerCase(), password, fullName, role);

        // Act - Check existence with different cases
        boolean existsLower = userService.usernameExists(username.toLowerCase());
        boolean existsUpper = userService.usernameExists(username.toUpperCase());
        boolean existsMixed = userService.usernameExists(username);

        // Assert
        assertTrue(existsLower, "Username should exist (lowercase)");
        assertTrue(existsUpper, "Username should exist (uppercase)");
        assertTrue(existsMixed, "Username should exist (mixed case)");
    }

    @Test
    void testRoleCaseInsensitivity() {
        // Arrange
        String username = "rolecase_" + System.currentTimeMillis();
        String password = "pass123";
        String fullName = "Role Case User";
        String role = "student";

        // Register with lowercase role
        userService.registerUser(username, password, fullName, role.toLowerCase());

        // Act - Login with different case roles
        User loginLower = userService.authenticate(username, password, role.toLowerCase());
        User loginUpper = userService.authenticate(username, password, role.toUpperCase());
        User loginMixed = userService.authenticate(username, password, "StUdEnT");

        // Assert
        assertNotNull(loginLower, "Login should succeed with lowercase role");
        assertNotNull(loginUpper, "Login should succeed with uppercase role");
        assertNotNull(loginMixed, "Login should succeed with mixed case role");
    }

    @Test
    void testUsernameExistsWithNull() {
        // Act
        boolean result = userService.usernameExists(null);

        // Assert
        assertFalse(result, "UsernameExists should return false for null");
    }

    @Test
    void testUsernameExistsWithNonExistentUser() {
        // Act
        boolean result = userService.usernameExists("nonexistentuser12345");

        // Assert
        assertFalse(result, "UsernameExists should return false for non-existent user");
    }

    @Test
    void testRegistrationWithWhitespaceTrimming() {
        // Arrange
        String username = "whitespace_" + System.currentTimeMillis();
        String password = "pass123";
        String fullName = "  Trimmed User  ";
        String role = "student";

        // Act
        boolean result = userService.registerUser("  " + username + "  ", password, fullName, role);

        // Assert
        assertTrue(result, "Registration should succeed with whitespace");
        assertTrue(userService.usernameExists(username), "Username should be normalized (trimmed and lowercased)");
        
        User user = userService.authenticate(username, password, role);
        assertNotNull(user, "User should be able to login with trimmed username");
        assertEquals("Trimmed User", user.getFullName(), "Full name should be trimmed");
    }

    @Test
    void testLoginWithWhitespaceInUsername() {
        // Arrange
        String username = "whitespacelogin_" + System.currentTimeMillis();
        String password = "pass123";
        String fullName = "Test User";
        String role = "student";

        userService.registerUser(username, password, fullName, role);

        // Act - Try to login with whitespace
        User result = userService.authenticate("  " + username + "  ", password, role);

        // Assert
        assertNotNull(result, "Login should succeed with whitespace in username (should be trimmed)");
    }
}