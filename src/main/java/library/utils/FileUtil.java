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

public class FileUtil {

    private static final String USERS_FILE_PATH = "data/users.json";
    private static final String BOOKS_FILE_PATH = "data/books.json";
    private static final String BORROWED_BOOKS_FILE_PATH = "data/borrowed_books.json";
    private static final String NOTIFICATIONS_FILE_PATH = "data/notifications.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    public static void writeUsers(List<User> users) {
        try {
            String json = gson.toJson(users);
            FileUtils.writeStringToFile(new File(USERS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void writeBooks(List<Book> books) {
        try {
            String json = gson.toJson(books);
            FileUtils.writeStringToFile(new File(BOOKS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
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

    public static void writeBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        try {
            String json = gson.toJson(borrowedBooks);
            FileUtils.writeStringToFile(new File(BORROWED_BOOKS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void writeNotifications(List<Notification> notifications) {
        try {
            String json = gson.toJson(notifications);
            FileUtils.writeStringToFile(new File(NOTIFICATIONS_FILE_PATH), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
