package library;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Base class for tests that need to mutate the JSON persistence files. It
 * automatically snapshots the original contents before each test and restores
 * them afterwards.
 */
public abstract class AbstractDataFileTest {

    protected static final Path USERS = Path.of("data", "users.json");
    protected static final Path BOOKS = Path.of("data", "books.json");
    protected static final Path BORROWED = Path.of("data", "borrowed_books.json");
    protected static final Path NOTIFICATIONS = Path.of("data", "notifications.json");

    private String usersBackup;
    private String booksBackup;
    private String borrowedBackup;
    private String notificationsBackup;

    @BeforeEach
    void backupDataFiles() throws IOException {
        usersBackup = TestDataHelper.backup(USERS);
        booksBackup = TestDataHelper.backup(BOOKS);
        borrowedBackup = TestDataHelper.backup(BORROWED);
        notificationsBackup = TestDataHelper.backup(NOTIFICATIONS);
    }

    @AfterEach
    void restoreDataFiles() throws IOException {
        TestDataHelper.restore(USERS, usersBackup);
        TestDataHelper.restore(BOOKS, booksBackup);
        TestDataHelper.restore(BORROWED, borrowedBackup);
        TestDataHelper.restore(NOTIFICATIONS, notificationsBackup);
    }
}



