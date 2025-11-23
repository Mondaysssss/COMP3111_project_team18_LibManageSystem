package library.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTest {

    @Test
    void defaultConstructorShouldMarkUnread() {
        Notification notification = new Notification();

        assertFalse(notification.isRead());
    }

    @Test
    void parameterizedConstructorShouldPopulateFields() {
        Notification notification = new Notification("Message", "author");

        assertEquals("Message", notification.getMessage());
        assertEquals("author", notification.getAuthorUsername());
        assertFalse(notification.isRead());
    }

    @Test
    void settersShouldUpdateValues() {
        Notification notification = new Notification();

        notification.setMessage("Updated");
        notification.setAuthorUsername("user");
        notification.setRead(true);

        assertEquals("Updated", notification.getMessage());
        assertEquals("user", notification.getAuthorUsername());
        assertTrue(notification.isRead());
    }
}



