package library;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import library.models.Book;
import library.models.Notification;
import library.models.User;
import library.utils.FileUtil;

/**
 * High level regression tests that exercise the core behaviours required by the
 * Author Portal specification. Each test focuses on one of the tasks that the
 * Author Dashboard must support.
 */
public class AuthorPortalFeaturesTest {

    private static final Path USERS_PATH = Path.of("data", "users.json");
    private static final Path BOOKS_PATH = Path.of("data", "books.json");
    private static final Path NOTIFICATIONS_PATH = Path.of("data", "notifications.json");

    private String usersBackup;
    private String booksBackup;
    private String notificationsBackup;

    @BeforeEach
    void backupFiles() throws IOException {
        usersBackup = readFileIfExists(USERS_PATH);
        booksBackup = readFileIfExists(BOOKS_PATH);
        notificationsBackup = readFileIfExists(NOTIFICATIONS_PATH);
    }

    @AfterEach
    void restoreFiles() throws IOException {
        restoreFile(USERS_PATH, usersBackup);
        restoreFile(BOOKS_PATH, booksBackup);
        restoreFile(NOTIFICATIONS_PATH, notificationsBackup);
    }

    @Test
    void testHandleAuthorRegistrationAndLoginTask() {
        String username = "author_test_user";
        List<User> users = FileUtil.readUsers();
        users.removeIf(u -> username.equals(u.getUsername()));
        FileUtil.writeUsers(users);

        User newUser = new User(username, "p@ssword", "Author Test", "author");
        users.add(newUser);
        FileUtil.writeUsers(users);

        List<User> reloaded = FileUtil.readUsers();
        assertTrue(
            reloaded.stream().anyMatch(u -> username.equals(u.getUsername())
                && "p@ssword".equals(u.getPassword())
                && "author".equals(u.getRole())),
            "Registered author should be persisted and available for subsequent login checks."
        );
    }

    @Test
    void testPublishedBookScreenTaskShowsCorrectStatuses() {
        List<Book> books = new ArrayList<>();
        books.add(buildBook("Approved Story", "author_portal", "approved", 3));
        books.add(buildBook("Pending Story", "author_portal", "pending", 0));
        books.add(buildBook("Rejected Story", "author_portal", "rejected", 0));
        books.add(buildBook("Other Author Story", "other_author", "approved", 1));
        FileUtil.writeBooks(books);

        List<String> statuses = FileUtil.readBooks().stream()
            .filter(b -> "author_portal".equals(b.getAuthorUsername()))
            .map(Book::getStatus)
            .collect(Collectors.toList());

        assertEquals(List.of("approved", "pending", "rejected"), statuses,
            "My Books tab should list every status for the current author.");
    }

    @Test
    void testPublishNewBookTaskPersistsPendingEntry() {
        String author = "author_publish";
        List<Book> baseline = FileUtil.readBooks();
        baseline.removeIf(book -> author.equals(book.getAuthorUsername()));
        FileUtil.writeBooks(baseline);

        Book drafted = new Book("Brand New Book", "Publish Author", "Summary", "Full content");
        drafted.setAuthorUsername(author);
        List<Book> updated = FileUtil.readBooks();
        updated.add(drafted);
        FileUtil.writeBooks(updated);

        List<Book> reloaded = FileUtil.readBooks().stream()
            .filter(book -> author.equals(book.getAuthorUsername()))
            .collect(Collectors.toList());

        assertEquals(1, reloaded.size());
        assertEquals("pending", reloaded.get(0).getStatus(), "Newly published books await librarian approval.");
    }

    @Test
    void testViewStatsScreenTaskCalculations() {
        String author = "author_stats";
        List<Book> books = new ArrayList<>();
        books.add(buildBookWithBorrowCount("One", author, "approved", 5));
        books.add(buildBookWithBorrowCount("Two", author, "approved", 2));
        books.add(buildBookWithBorrowCount("Three", author, "pending", 0));
        books.add(buildBookWithBorrowCount("Four", author, "pending", 0));
        FileUtil.writeBooks(books);

        List<Book> authorBooks = FileUtil.readBooks().stream()
            .filter(b -> author.equals(b.getAuthorUsername()))
            .collect(Collectors.toList());

        long pending = authorBooks.stream().filter(b -> "pending".equals(b.getStatus())).count();
        long approved = authorBooks.stream().filter(b -> "approved".equals(b.getStatus())).count();
        List<Book> topByBorrow = authorBooks.stream()
            .sorted(Comparator.comparingInt(Book::getBorrowCount).reversed())
            .limit(5)
            .collect(Collectors.toList());

        assertEquals(2, pending);
        assertEquals(2, approved);
        assertEquals("One", topByBorrow.get(0).getTitle(), "Top borrowed book should lead the chart.");
        assertEquals(5, topByBorrow.get(0).getBorrowCount());
    }

    @Test
    void testManageProfileScreenTaskUpdatesUserDetails() {
        String username = "profile_author";
        List<User> users = FileUtil.readUsers();
        users.removeIf(user -> username.equals(user.getUsername()));
        users.add(new User(username, "oldPass", "Old Name", "author"));
        FileUtil.writeUsers(users);

        List<User> reloaded = FileUtil.readUsers();
        for (User user : reloaded) {
            if (username.equals(user.getUsername())) {
                user.setFullName("New Fancy Name");
                user.setPassword("newPass123");
            }
        }
        FileUtil.writeUsers(reloaded);

        User updated = FileUtil.readUsers().stream()
            .filter(user -> username.equals(user.getUsername()))
            .findFirst()
            .orElseThrow();

        assertEquals("New Fancy Name", updated.getFullName());
        assertEquals("newPass123", updated.getPassword());
    }

    @Test
    void testNotificationBoardTaskStoresMessages() {
        String author = "notify_author";
        List<Notification> notifications = FileUtil.readNotifications();
        notifications.removeIf(n -> author.equals(n.getAuthorUsername()));
        FileUtil.writeNotifications(notifications);

        notifications = FileUtil.readNotifications();
        notifications.add(new Notification("Book approved", author));
        notifications.add(new Notification("Book rejected", author));
        FileUtil.writeNotifications(notifications);

        List<String> messages = FileUtil.readNotifications().stream()
            .filter(n -> author.equals(n.getAuthorUsername()))
            .map(Notification::getMessage)
            .collect(Collectors.toList());

        assertEquals(List.of("Book approved", "Book rejected"), messages,
            "Inform Board should surface the latest notifications for an author.");
    }

    private static Book buildBook(String title, String authorUsername, String status, int borrowCount) {
        Book book = new Book(title, "Author Name", "Abstract", "Content");
        book.setAuthorUsername(authorUsername);
        book.setStatus(status);
        book.setBorrowCount(borrowCount);
        return book;
    }

    private static Book buildBookWithBorrowCount(String title, String authorUsername, String status, int borrowCount) {
        Book book = buildBook(title, authorUsername, status, borrowCount);
        book.setPublishedDate(new Date());
        return book;
    }

    private static String readFileIfExists(Path path) throws IOException {
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        return null;
    }

    private static void restoreFile(Path path, String backup) throws IOException {
        if (backup == null) {
            Files.deleteIfExists(path);
        } else {
            Files.createDirectories(path.getParent());
            Files.writeString(path, backup, StandardCharsets.UTF_8);
        }
    }
}

