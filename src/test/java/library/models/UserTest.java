package library.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testGettersSettersAndToString() {
        User u = new User("alice", "secret", "Alice Liddell", "student");
        // getters
        assertEquals("alice", u.getUsername());
        assertEquals("secret", u.getPassword());
        assertEquals("Alice Liddell", u.getFullName());
        assertEquals("student", u.getRole());

        // setters
        u.setPassword("newpass");
        u.setFullName("Alice Wonderland");
        u.setRole("author");
        assertEquals("newpass", u.getPassword());
        assertEquals("Alice Wonderland", u.getFullName());
        assertEquals("author", u.getRole());

        // toString
        String s = u.toString();
        assertTrue(s.contains("alice"));
        assertTrue(s.contains("Alice Wonderland"));
        assertTrue(s.contains("author"));
    }

    @Test
    void testEqualsAndHashCode() {
        User a = new User("bob", "pw1", "Bob Builder", "librarian");
        User b = new User("bob", "pw2", "Bobby", "student"); // same username -> equal
        User c = new User("charlie", "pw3", "Charlie", "author");

        // equals by username only
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
        // null and different class checks
        assertNotEquals(a, null);
        assertNotEquals(a, "not a user");
    }
}