package library.utils;

import library.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CurrentUserTest {

    @AfterEach
    void clear() {
        CurrentUser.setCurrentUser(null);
    }

    @Test
    void shouldReturnNullByDefault() {
        assertNull(CurrentUser.getCurrentUser());
    }

    @Test
    void shouldHoldReferenceToSetUser() {
        User user = new User("user", "pass", "User Name", "student");

        CurrentUser.setCurrentUser(user);

        assertEquals(user, CurrentUser.getCurrentUser());
    }
}



