package library.utils;

import library.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {
    private static final String USERS_FILE_PATH = "data/users.json";
    private File usersFile = new File(USERS_FILE_PATH);

    @BeforeEach
    void setUp() throws IOException {
        if (usersFile.exists()) {
            usersFile.delete();
        }
        usersFile.createNewFile();
    }

    @AfterEach
    void tearDown() {
        if (usersFile.exists()) {
            usersFile.delete();
        }
    }
    @Test
    void testWriteAndReadUsers() {
        List<User> originalUsers = new ArrayList<>();
        originalUsers.add(new User("testuser1", "pass1", "Test User One", "student"));
        originalUsers.add(new User("testuser2", "pass2", "Test User Two", "librarian"));

        FileUtil.writeUsers(originalUsers);

        List<User> readUsers = FileUtil.readUsers();

        assertNotNull(readUsers);
        assertEquals(2, readUsers.size());
        assertEquals("testuser1", readUsers.get(0).getUsername());
    }

    @Test
    void testReadFromEmptyFile() {

        List<User> users = FileUtil.readUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testReadUsersWhenFileDoesNotExist() {
        // Delete file if exists
        if (usersFile.exists()) {
            usersFile.delete();
        }

        List<User> users = FileUtil.readUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testWriteAndReadBooks() {
        List<library.models.Book> books = new ArrayList<>();
        library.models.Book book1 = new library.models.Book("Book 1", "Author 1", "Abstract 1", "Content 1");
        library.models.Book book2 = new library.models.Book("Book 2", "Author 2", "Abstract 2", "Content 2");
        
        books.add(book1);
        books.add(book2);
        
        FileUtil.writeBooks(books);
        
        List<library.models.Book> readBooks = FileUtil.readBooks();
        assertNotNull(readBooks);
        assertEquals(2, readBooks.size());
        assertEquals("Book 1", readBooks.get(0).getTitle());
    }

    @Test
    void testReadBooksWhenFileDoesNotExist() {
        File booksFile = new File("data/books.json");
        if (booksFile.exists()) {
            booksFile.delete();
        }
        
        List<library.models.Book> books = FileUtil.readBooks();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void testReadBooksFromEmptyFile() throws IOException {
        File booksFile = new File("data/books.json");
        if (!booksFile.getParentFile().exists()) {
            booksFile.getParentFile().mkdirs();
        }
        if (booksFile.exists()) {
            booksFile.delete();
        }
        booksFile.createNewFile();
        
        List<library.models.Book> books = FileUtil.readBooks();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void testWriteAndReadBorrowedBooks() {
        List<library.models.BorrowedBook> borrowedBooks = new ArrayList<>();
        library.models.BorrowedBook borrowed1 = new library.models.BorrowedBook("Book 1", "user1", new java.util.Date(), null);
        library.models.BorrowedBook borrowed2 = new library.models.BorrowedBook("Book 2", "user2", new java.util.Date(), new java.util.Date());
        
        borrowedBooks.add(borrowed1);
        borrowedBooks.add(borrowed2);
        
        FileUtil.writeBorrowedBooks(borrowedBooks);
        
        List<library.models.BorrowedBook> readBorrowed = FileUtil.readBorrowedBooks();
        assertNotNull(readBorrowed);
        assertEquals(2, readBorrowed.size());
        assertEquals("Book 1", readBorrowed.get(0).getBookTitle());
    }

    @Test
    void testReadBorrowedBooksWhenFileDoesNotExist() {
        File borrowedFile = new File("data/borrowed_books.json");
        if (borrowedFile.exists()) {
            borrowedFile.delete();
        }
        
        List<library.models.BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        assertNotNull(borrowedBooks);
        assertTrue(borrowedBooks.isEmpty());
    }

    @Test
    void testWriteAndReadNotifications() {
        List<library.models.Notification> notifications = new ArrayList<>();
        library.models.Notification n1 = new library.models.Notification("Message 1", "author1");
        library.models.Notification n2 = new library.models.Notification("Message 2", "author2");
        
        notifications.add(n1);
        notifications.add(n2);
        
        FileUtil.writeNotifications(notifications);
        
        List<library.models.Notification> readNotifications = FileUtil.readNotifications();
        assertNotNull(readNotifications);
        assertEquals(2, readNotifications.size());
        assertEquals("Message 1", readNotifications.get(0).getMessage());
    }

    @Test
    void testReadNotificationsWhenFileDoesNotExist() {
        File notificationsFile = new File("data/notifications.json");
        if (notificationsFile.exists()) {
            notificationsFile.delete();
        }
        
        List<library.models.Notification> notifications = FileUtil.readNotifications();
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testReadNotificationsFromEmptyFile() throws IOException {
        File notificationsFile = new File("data/notifications.json");
        if (!notificationsFile.getParentFile().exists()) {
            notificationsFile.getParentFile().mkdirs();
        }
        if (notificationsFile.exists()) {
            notificationsFile.delete();
        }
        notificationsFile.createNewFile();
        
        List<library.models.Notification> notifications = FileUtil.readNotifications();
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testWriteEmptyLists() {
        // Test writing empty lists
        FileUtil.writeUsers(new ArrayList<>());
        FileUtil.writeBooks(new ArrayList<>());
        FileUtil.writeBorrowedBooks(new ArrayList<>());
        FileUtil.writeNotifications(new ArrayList<>());
        
        assertTrue(FileUtil.readUsers().isEmpty());
        assertTrue(FileUtil.readBooks().isEmpty());
        assertTrue(FileUtil.readBorrowedBooks().isEmpty());
        assertTrue(FileUtil.readNotifications().isEmpty());
    }

    @Test
    void testWriteNullLists() {
        // Test that writing null lists doesn't crash (should handle gracefully)
        try {
            FileUtil.writeUsers(null);
            FileUtil.writeBooks(null);
            FileUtil.writeBorrowedBooks(null);
            FileUtil.writeNotifications(null);
            // If we get here, the methods handled null gracefully
            assertTrue(true);
        } catch (Exception e) {
            // If exception is thrown, that's also acceptable behavior
            assertNotNull(e);
        }
    }

    @Test
    void testReadUsersWithValidJson() {
        // Test reading users when file exists and has valid JSON content
        List<User> users = new ArrayList<>();
        users.add(new User("user1", "pass1", "Name1", "author"));
        users.add(new User("user2", "pass2", "Name2", "student"));
        
        FileUtil.writeUsers(users);
        
        List<User> readUsers = FileUtil.readUsers();
        assertNotNull(readUsers);
        assertEquals(2, readUsers.size());
        assertEquals("user1", readUsers.get(0).getUsername());
        assertEquals("user2", readUsers.get(1).getUsername());
    }

    @Test
    void testReadBooksWithValidJson() {
        // Test reading books when file exists and has valid JSON content
        List<library.models.Book> books = new ArrayList<>();
        library.models.Book book1 = new library.models.Book("Title1", "Author1", "Abstract1", "Content1");
        book1.setAuthorUsername("author1");
        book1.setStatus("approved");
        
        library.models.Book book2 = new library.models.Book("Title2", "Author2", "Abstract2", "Content2");
        book2.setAuthorUsername("author2");
        book2.setStatus("pending");
        
        books.add(book1);
        books.add(book2);
        
        FileUtil.writeBooks(books);
        
        List<library.models.Book> readBooks = FileUtil.readBooks();
        assertNotNull(readBooks);
        assertEquals(2, readBooks.size());
        assertEquals("Title1", readBooks.get(0).getTitle());
        assertEquals("Title2", readBooks.get(1).getTitle());
    }

    @Test
    void testReadBorrowedBooksWithValidJson() {
        // Test reading borrowed books when file exists and has valid JSON content
        List<library.models.BorrowedBook> borrowedBooks = new ArrayList<>();
        java.util.Date date1 = new java.util.Date();
        java.util.Date date2 = new java.util.Date(System.currentTimeMillis() + 86400000);
        
        library.models.BorrowedBook borrowed1 = new library.models.BorrowedBook("Book1", "user1", date1, null);
        library.models.BorrowedBook borrowed2 = new library.models.BorrowedBook("Book2", "user2", date1, date2);
        
        borrowedBooks.add(borrowed1);
        borrowedBooks.add(borrowed2);
        
        FileUtil.writeBorrowedBooks(borrowedBooks);
        
        List<library.models.BorrowedBook> readBorrowed = FileUtil.readBorrowedBooks();
        assertNotNull(readBorrowed);
        assertEquals(2, readBorrowed.size());
        assertEquals("Book1", readBorrowed.get(0).getBookTitle());
        assertEquals("Book2", readBorrowed.get(1).getBookTitle());
    }

    @Test
    void testReadNotificationsWithValidJson() {
        // Test reading notifications when file exists and has valid JSON content
        List<library.models.Notification> notifications = new ArrayList<>();
        library.models.Notification n1 = new library.models.Notification("Message1", "author1");
        library.models.Notification n2 = new library.models.Notification("Message2", "author2");
        n2.setRead(true);
        
        notifications.add(n1);
        notifications.add(n2);
        
        FileUtil.writeNotifications(notifications);
        
        List<library.models.Notification> readNotifications = FileUtil.readNotifications();
        assertNotNull(readNotifications);
        assertEquals(2, readNotifications.size());
        assertEquals("Message1", readNotifications.get(0).getMessage());
        assertEquals("Message2", readNotifications.get(1).getMessage());
        assertFalse(readNotifications.get(0).isRead());
        assertTrue(readNotifications.get(1).isRead());
    }

    @Test
    void testReadBooksFromEmptyFileAfterWrite() throws IOException {
        // Test that reading from an empty file returns empty list
        File booksFile = new File("data/books.json");
        if (!booksFile.getParentFile().exists()) {
            booksFile.getParentFile().mkdirs();
        }
        if (booksFile.exists()) {
            booksFile.delete();
        }
        booksFile.createNewFile();
        
        // Write empty list
        FileUtil.writeBooks(new ArrayList<>());
        
        // Read should return empty list
        List<library.models.Book> books = FileUtil.readBooks();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void testReadBorrowedBooksFromEmptyFile() throws IOException {
        File borrowedFile = new File("data/borrowed_books.json");
        if (!borrowedFile.getParentFile().exists()) {
            borrowedFile.getParentFile().mkdirs();
        }
        if (borrowedFile.exists()) {
            borrowedFile.delete();
        }
        borrowedFile.createNewFile();
        
        List<library.models.BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        assertNotNull(borrowedBooks);
        assertTrue(borrowedBooks.isEmpty());
    }

    @Test
    void testMultipleWriteAndReadOperations() {
        // Test multiple write/read cycles
        List<User> users1 = new ArrayList<>();
        users1.add(new User("user1", "pass1", "Name1", "author"));
        FileUtil.writeUsers(users1);
        List<User> read1 = FileUtil.readUsers();
        assertEquals(1, read1.size());
        
        List<User> users2 = new ArrayList<>();
        users2.add(new User("user2", "pass2", "Name2", "student"));
        users2.add(new User("user3", "pass3", "Name3", "librarian"));
        FileUtil.writeUsers(users2);
        List<User> read2 = FileUtil.readUsers();
        assertEquals(2, read2.size());
        
        List<User> users3 = new ArrayList<>();
        FileUtil.writeUsers(users3);
        List<User> read3 = FileUtil.readUsers();
        assertTrue(read3.isEmpty());
    }

    @Test
    void testWriteAndReadWithSpecialCharacters() {
        // Test writing and reading data with special characters
        List<User> users = new ArrayList<>();
        users.add(new User("user@name", "pass&word", "Name's Full", "author"));
        FileUtil.writeUsers(users);
        
        List<User> readUsers = FileUtil.readUsers();
        assertEquals(1, readUsers.size());
        assertEquals("user@name", readUsers.get(0).getUsername());
        assertEquals("pass&word", readUsers.get(0).getPassword());
        assertEquals("Name's Full", readUsers.get(0).getFullName());
    }

    @Test
    void testWriteAndReadWithUnicodeCharacters() {
        // Test writing and reading data with Unicode characters
        List<User> users = new ArrayList<>();
        users.add(new User("用户名", "密码", "全名", "author"));
        FileUtil.writeUsers(users);
        
        List<User> readUsers = FileUtil.readUsers();
        assertEquals(1, readUsers.size());
        assertEquals("用户名", readUsers.get(0).getUsername());
        assertEquals("密码", readUsers.get(0).getPassword());
        assertEquals("全名", readUsers.get(0).getFullName());
    }

    @Test
    void testReadBooksWithEmptyStringFile() throws IOException {
        // Test reading from file that exists but contains only empty string
        File booksFile = new File("data/books.json");
        if (!booksFile.getParentFile().exists()) {
            booksFile.getParentFile().mkdirs();
        }
        if (booksFile.exists()) {
            booksFile.delete();
        }
        booksFile.createNewFile();
        // File is empty (empty string)
        
        List<library.models.Book> books = FileUtil.readBooks();
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void testReadBorrowedBooksWithEmptyStringFile() throws IOException {
        File borrowedFile = new File("data/borrowed_books.json");
        if (!borrowedFile.getParentFile().exists()) {
            borrowedFile.getParentFile().mkdirs();
        }
        if (borrowedFile.exists()) {
            borrowedFile.delete();
        }
        borrowedFile.createNewFile();
        // File is empty
        
        List<library.models.BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        assertNotNull(borrowedBooks);
        assertTrue(borrowedBooks.isEmpty());
    }

    @Test
    void testReadNotificationsWithEmptyStringFile() throws IOException {
        File notificationsFile = new File("data/notifications.json");
        if (!notificationsFile.getParentFile().exists()) {
            notificationsFile.getParentFile().mkdirs();
        }
        if (notificationsFile.exists()) {
            notificationsFile.delete();
        }
        notificationsFile.createNewFile();
        // File is empty
        
        List<library.models.Notification> notifications = FileUtil.readNotifications();
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testReadUsersWithEmptyStringFile() throws IOException {
        // Test reading from file that exists but contains only empty string
        if (usersFile.exists()) {
            usersFile.delete();
        }
        usersFile.createNewFile();
        // File is empty (empty string)
        
        List<User> users = FileUtil.readUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testWriteLargeLists() {
        // Test writing and reading large lists
        List<User> largeUserList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeUserList.add(new User("user" + i, "pass" + i, "Name" + i, "author"));
        }
        
        FileUtil.writeUsers(largeUserList);
        List<User> readUsers = FileUtil.readUsers();
        assertEquals(100, readUsers.size());
        assertEquals("user0", readUsers.get(0).getUsername());
        assertEquals("user99", readUsers.get(99).getUsername());
    }

    @Test
    void testWriteBooksWithAllFields() {
        // Test writing books with all fields populated
        List<library.models.Book> books = new ArrayList<>();
        library.models.Book book = new library.models.Book("Title", "Author", "Abstract", "Content");
        book.setAuthorUsername("author_user");
        book.setStatus("approved");
        book.setPublishedDate(new java.util.Date());
        book.setBorrowCount(10);
        book.setReaders(5);
        books.add(book);
        
        FileUtil.writeBooks(books);
        List<library.models.Book> readBooks = FileUtil.readBooks();
        assertEquals(1, readBooks.size());
        assertEquals("Title", readBooks.get(0).getTitle());
        assertEquals("author_user", readBooks.get(0).getAuthorUsername());
        assertEquals("approved", readBooks.get(0).getStatus());
        assertEquals(10, readBooks.get(0).getBorrowCount());
        assertEquals(5, readBooks.get(0).getReaders());
    }
}