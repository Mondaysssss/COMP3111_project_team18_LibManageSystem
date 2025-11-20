package library.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void constructorShouldSetDefaultStatus() {
        User user = new User("username", "password", "Full Name", "student");

        assertEquals("username", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("Full Name", user.getFullName());
        assertEquals("student", user.getRole());
        assertEquals("active", user.getStatus());
    }

    @Test
    void settersShouldUpdateValues() {
        User user = new User("username", "password", "Full Name", "student");

        user.setUsername("newUser");
        user.setPassword("newPass");
        user.setFullName("New Name");
        user.setRole("author");
        user.setStatus("inactive");

        assertEquals("newUser", user.getUsername());
        assertEquals("newPass", user.getPassword());
        assertEquals("New Name", user.getFullName());
        assertEquals("author", user.getRole());
        assertEquals("inactive", user.getStatus());
    }
}



