package library;

import library.models.Book;
import library.models.Notification;
import library.models.User;
import library.utils.FileUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration scenarios that exercise the librarian responsibilities without
 * going through the JavaFX layer. They focus on the persistence effects that
 * the dashboard is expected to trigger.
 */
class LibrarianPortalFeaturesTest extends AbstractDataFileTest {

    @Test
    void approvingBookShouldPersistStatusAndNotification() {
        Book pending = new Book("Review Book", "Author Full", "Abstract", "Body");
        pending.setAuthorUsername("author_account");
        pending.setStatus("pending");
        FileUtil.writeBooks(new ArrayList<>(List.of(pending)));
        FileUtil.writeNotifications(new ArrayList<>());

        List<Book> books = FileUtil.readBooks();
        Book target = books.get(0);
        target.setStatus("approved");
        if (target.getPublishedDate() == null) {
            target.setPublishedDate(new Date());
        }
        List<Notification> notifications = FileUtil.readNotifications();
        notifications.add(new Notification("Your book \"" + target.getTitle() + "\" has been approved.", target.getAuthorUsername()));
        FileUtil.writeBooks(books);
        FileUtil.writeNotifications(notifications);

        Book reloaded = FileUtil.readBooks().get(0);
        assertEquals("approved", reloaded.getStatus());
        assertNotNull(reloaded.getPublishedDate(), "Approval should stamp a published date.");

        boolean notified = FileUtil.readNotifications().stream()
            .anyMatch(n -> "author_account".equals(n.getAuthorUsername())
                && n.getMessage().contains("approved"));
        assertTrue(notified, "Approving a book should notify the author.");
    }

    @Test
    void rejectingBookShouldPersistStatusAndNotification() {
        Book pending = new Book("Reject Me", "Author", "Abstract", "Body");
        pending.setAuthorUsername("author_reject");
        pending.setStatus("pending");
        FileUtil.writeBooks(new ArrayList<>(List.of(pending)));
        FileUtil.writeNotifications(new ArrayList<>());

        List<Book> books = FileUtil.readBooks();
        Book target = books.get(0);
        target.setStatus("rejected");
        List<Notification> notifications = FileUtil.readNotifications();
        notifications.add(new Notification("Your book \"" + target.getTitle() + "\" has been rejected.", target.getAuthorUsername()));
        FileUtil.writeBooks(books);
        FileUtil.writeNotifications(notifications);

        Book reloaded = FileUtil.readBooks().get(0);
        assertEquals("rejected", reloaded.getStatus());
        boolean notified = FileUtil.readNotifications().stream()
            .anyMatch(n -> "author_reject".equals(n.getAuthorUsername())
                && n.getMessage().contains("rejected"));
        assertTrue(notified, "Rejecting a book should notify the author.");
    }

    @Test
    void togglingUserStatusShouldSwitchBetweenActiveAndInactive() {
        User user = new User("student_toggle", "pass", "Student Toggle", "student");
        user.setStatus("active");
        FileUtil.writeUsers(new ArrayList<>(List.of(user)));

        List<User> users = FileUtil.readUsers();
        User managed = users.get(0);
        managed.setStatus("inactive");
        FileUtil.writeUsers(users);

        String firstStatus = FileUtil.readUsers().get(0).getStatus();
        assertEquals("inactive", firstStatus);

        users = FileUtil.readUsers();
        users.get(0).setStatus("active");
        FileUtil.writeUsers(users);

        String secondStatus = FileUtil.readUsers().get(0).getStatus();
        assertEquals("active", secondStatus);
    }

    @Test
    void updatingProfileShouldPersistNameAndPassword() {
        User librarian = new User("librarian_user", "oldPass", "Old Name", "librarian");
        FileUtil.writeUsers(new ArrayList<>(List.of(librarian)));

        List<User> users = FileUtil.readUsers();
        User profile = users.get(0);
        profile.setFullName("New Librarian Name");
        profile.setPassword("newPass123");
        FileUtil.writeUsers(users);

        User updated = FileUtil.readUsers().get(0);
        assertEquals("New Librarian Name", updated.getFullName());
        assertEquals("newPass123", updated.getPassword());
    }
}



