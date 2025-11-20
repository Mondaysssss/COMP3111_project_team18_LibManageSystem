package library.utils;

import library.AbstractDataFileTest;
import library.models.Book;
import library.models.BorrowedBook;
import library.models.Notification;
import library.models.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest extends AbstractDataFileTest {

    @Test
    void writeAndReadUsersShouldRoundTrip() {
        List<User> users = new ArrayList<>();
        users.add(new User("student", "pass", "Student", "student"));
        users.add(new User("author", "pass", "Author", "author"));

        FileUtil.writeUsers(users);

        List<User> reloaded = FileUtil.readUsers();
        assertEquals(2, reloaded.size());
        assertEquals("student", reloaded.get(0).getUsername());
    }

    @Test
    void writeAndReadBooksShouldRoundTrip() {
        Book approved = new Book("Book A", "Author", "Abstract", "Content");
        approved.setAuthorUsername("author");
        approved.setStatus("approved");
        approved.setPublishedDate(new Date());
        approved.setBorrowCount(5);

        List<Book> books = List.of(approved);
        FileUtil.writeBooks(books);

        List<Book> reloaded = FileUtil.readBooks();
        assertEquals(1, reloaded.size());
        assertEquals("Book A", reloaded.get(0).getTitle());
        assertEquals(5, reloaded.get(0).getBorrowCount());
    }

    @Test
    void writeAndReadBorrowedBooksShouldRoundTrip() {
        BorrowedBook borrowedBook = new BorrowedBook("Book A", "student", new Date(), new Date());
        List<BorrowedBook> borrowedBooks = List.of(borrowedBook);

        FileUtil.writeBorrowedBooks(borrowedBooks);

        List<BorrowedBook> reloaded = FileUtil.readBorrowedBooks();
        assertEquals(1, reloaded.size());
        assertEquals("student", reloaded.get(0).getBorrowerUsername());
    }

    @Test
    void writeAndReadNotificationsShouldRoundTrip() {
        Notification notification = new Notification("Message", "author");
        List<Notification> notifications = List.of(notification);

        FileUtil.writeNotifications(notifications);

        List<Notification> reloaded = FileUtil.readNotifications();
        assertEquals(1, reloaded.size());
        assertEquals("Message", reloaded.get(0).getMessage());
    }

    @Test
    void readUsersReturnsEmptyWhenFileMissing() throws IOException {
        Files.deleteIfExists(USERS);

        List<User> users = FileUtil.readUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void hasActiveBookWithTitleDetectsPendingDuplicate() {
        Book pending = new Book("Duplicated Title", "Author", "Abstract", "Content");
        pending.setAuthorUsername("dup_author");
        pending.setStatus("pending");

        FileUtil.writeBooks(List.of(pending));

        assertTrue(FileUtil.hasActiveBookWithTitle("dup_author", "duplicated title"));
    }

    @Test
    void hasActiveBookWithTitleIgnoresRejectedBooks() {
        Book rejected = new Book("Duplicated Title", "Author", "Abstract", "Content");
        rejected.setAuthorUsername("dup_author");
        rejected.setStatus("rejected");

        FileUtil.writeBooks(List.of(rejected));

        assertFalse(FileUtil.hasActiveBookWithTitle("dup_author", "Duplicated Title"));
    }
}