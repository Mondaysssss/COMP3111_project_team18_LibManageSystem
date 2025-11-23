package library.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import library.models.User;
import library.models.Book;
import library.models.BorrowedBook;
import library.models.Notification;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading and writing data to JSON files.
 * 
 * <p>This class provides static methods to persist and retrieve library data
 * including users, books, borrowed books, and notifications. All data is stored
 * in JSON format in the "data" directory using Gson for serialization/deserialization.
 * 
 * <p>The class handles file I/O operations gracefully, returning empty lists
 * if files don't exist or if errors occur during reading. All write operations
 * use pretty-printed JSON for human readability.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class FileUtil {

    private static final String USERS_FILE_PATH = "data/users.json";
    private static final String BOOKS_FILE_PATH = "data/books.json";
    private static final String BORROWED_BOOKS_FILE_PATH = "data/borrowed_books.json";
    private static final String NOTIFICATIONS_FILE_PATH = "data/notifications.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Reads all users from the users.json file.
     * 
     * @return a list of all users, or an empty list if the file doesn't exist
     *         or an error occurs during reading
     */
    public static List<User> readUsers() {
        try {
            File file = new File(USERS_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (json.isEmpty()) {
                return new ArrayList<>();
            }
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            return gson.fromJson(json, userListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Writes the list of users to the users.json file.
     * 
     * @param users the list of users to write
     */
    public static void writeUsers(List<User> users) {
        try {
            String json = gson.toJson(users);
            FileUtils.writeStringToFile(new File(USERS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all books from the books.json file.
     * 
     * @return a list of all books, or an empty list if the file doesn't exist
     *         or an error occurs during reading
     */
    public static List<Book> readBooks() {
        try {
            File file = new File(BOOKS_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (json.isEmpty()) {
                return new ArrayList<>();
            }
            Type bookListType = new TypeToken<ArrayList<Book>>(){}.getType();
            return gson.fromJson(json, bookListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Writes the list of books to the books.json file.
     * 
     * @param books the list of books to write
     */
    public static void writeBooks(List<Book> books) {
        try {
            String json = gson.toJson(books);
            FileUtils.writeStringToFile(new File(BOOKS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads all borrowed book records from the borrowed_books.json file.
     * 
     * @return a list of all borrowed book records, or an empty list if the file
     *         doesn't exist or an error occurs during reading
     */
    public static List<BorrowedBook> readBorrowedBooks() {
        try {
            File file = new File(BORROWED_BOOKS_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (json.isEmpty()) {
                return new ArrayList<>();
            }
            Type borrowedBookListType = new TypeToken<ArrayList<BorrowedBook>>(){}.getType();
            return gson.fromJson(json, borrowedBookListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Writes the list of borrowed book records to the borrowed_books.json file.
     * 
     * @param borrowedBooks the list of borrowed book records to write
     */
    public static void writeBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        try {
            String json = gson.toJson(borrowedBooks);
            FileUtils.writeStringToFile(new File(BORROWED_BOOKS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all notifications from the notifications.json file.
     * 
     * @return a list of all notifications, or an empty list if the file doesn't exist
     *         or an error occurs during reading
     */
    public static List<Notification> readNotifications() {
        try {
            File file = new File(NOTIFICATIONS_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            if (json.isEmpty()) {
                return new ArrayList<>();
            }
            Type notificationListType = new TypeToken<ArrayList<Notification>>(){}.getType();
            return gson.fromJson(json, notificationListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Writes the list of notifications to the notifications.json file.
     * 
     * @param notifications the list of notifications to write
     */
    public static void writeNotifications(List<Notification> notifications) {
        try {
            String json = gson.toJson(notifications);
            FileUtils.writeStringToFile(new File(NOTIFICATIONS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the given author already has a non-rejected book with the provided title.
     * This helper treats titles case-insensitively and ignores leading/trailing whitespace.
     *
     * @param authorUsername the username of the author to check
     * @param title the proposed title
     * @return true if a duplicate active (pending/approved) title exists
     */
    public static boolean hasActiveBookWithTitle(String authorUsername, String title) {
        return hasActiveBookWithTitle(readBooks(), authorUsername, title);
    }

    /**
     * Variant of {@link #hasActiveBookWithTitle(String, String)} that operates on a provided book list.
     */
    public static boolean hasActiveBookWithTitle(List<Book> books, String authorUsername, String title) {
        if (books == null || authorUsername == null || title == null) {
            return false;
        }

        String normalizedAuthor = authorUsername.trim();
        String normalizedTitle = title.trim();

        return books.stream()
                .filter(book -> book.getAuthorUsername() != null
                        && book.getAuthorUsername().trim().equals(normalizedAuthor))
                .filter(book -> book.getTitle() != null
                        && book.getTitle().trim().equalsIgnoreCase(normalizedTitle))
                .anyMatch(book -> {
                    String status = book.getStatus();
                    return status == null || !status.equalsIgnoreCase("rejected");
                });
    }
}
