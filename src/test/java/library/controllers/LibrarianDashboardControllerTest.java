package library.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import library.models.Book;
import library.models.Notification;
import library.models.User;
import library.utils.CurrentUser;
import library.utils.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;

public class LibrarianDashboardControllerTest {

    private static boolean toolkitInitialized;

    private LibrarianDashboardController controller;
    private MockedStatic<FileUtil> fileUtilMockedStatic;
    private MockedStatic<CurrentUser> currentUserMockedStatic;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        if (!toolkitInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
            toolkitInitialized = true;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new LibrarianDashboardController();
        fileUtilMockedStatic = mockStatic(FileUtil.class);
        currentUserMockedStatic = mockStatic(CurrentUser.class);

        User defaultLibrarian = new User("librarian", "password", "Librarian Name", "librarian");
        currentUserMockedStatic.when(CurrentUser::getCurrentUser).thenReturn(defaultLibrarian);

        initializeControllerFields();
    }

    @AfterEach
    void tearDown() {
        fileUtilMockedStatic.close();
        currentUserMockedStatic.close();
    }

    @Test
    void approveBook_ShouldChangeStatusToApprovedAndNotify() throws Exception {
        Book pendingBook = createPendingBook();
        List<Book> books = new ArrayList<>(List.of(pendingBook));

        List<Notification> notifications = new ArrayList<>();
        fileUtilMockedStatic.when(FileUtil::readBooks).thenReturn(books);
        fileUtilMockedStatic.when(FileUtil::readNotifications).thenReturn(notifications);

        Method approveBookMethod = LibrarianDashboardController.class.getDeclaredMethod("approveBook", Book.class);
        approveBookMethod.setAccessible(true);
        approveBookMethod.invoke(controller, pendingBook);

        assertEquals("approved", pendingBook.getStatus());
        assertNotNull(pendingBook.getPublishedDate());
        fileUtilMockedStatic.verify(() -> FileUtil.writeBooks(books));
        fileUtilMockedStatic.verify(() -> FileUtil.writeNotifications(notifications));
    }

    @Test
    void rejectBook_ShouldChangeStatusToRejectedAndNotify() throws Exception {
        Book pendingBook = createPendingBook();
        List<Book> books = new ArrayList<>(List.of(pendingBook));

        List<Notification> notifications = new ArrayList<>();
        fileUtilMockedStatic.when(FileUtil::readBooks).thenReturn(books);
        fileUtilMockedStatic.when(FileUtil::readNotifications).thenReturn(notifications);

        Method rejectBookMethod = LibrarianDashboardController.class.getDeclaredMethod("rejectBook", Book.class);
        rejectBookMethod.setAccessible(true);
        rejectBookMethod.invoke(controller, pendingBook);

        assertEquals("rejected", pendingBook.getStatus());
        fileUtilMockedStatic.verify(() -> FileUtil.writeBooks(books));
        fileUtilMockedStatic.verify(() -> FileUtil.writeNotifications(notifications));
    }

    @Test
    void toggleUserStatus_ShouldDeactivateActiveUser() throws Exception {
        User user = new User("student", "pass", "Student User", "student");
        user.setStatus("active");
        List<User> users = new ArrayList<>(List.of(user));

        fileUtilMockedStatic.when(FileUtil::readUsers).thenReturn(users);

        Method toggleUserStatusMethod = LibrarianDashboardController.class.getDeclaredMethod("toggleUserStatus", User.class);
        toggleUserStatusMethod.setAccessible(true);
        toggleUserStatusMethod.invoke(controller, user);

        assertEquals("inactive", user.getStatus());
        fileUtilMockedStatic.verify(() -> FileUtil.writeUsers(users));
    }

    @Test
    void toggleUserStatus_ShouldActivateInactiveUser() throws Exception {
        User user = new User("student", "pass", "Student User", "student");
        user.setStatus("inactive");
        List<User> users = new ArrayList<>(List.of(user));

        fileUtilMockedStatic.when(FileUtil::readUsers).thenReturn(users);

        Method toggleUserStatusMethod = LibrarianDashboardController.class.getDeclaredMethod("toggleUserStatus", User.class);
        toggleUserStatusMethod.setAccessible(true);
        toggleUserStatusMethod.invoke(controller, user);

        assertEquals("active", user.getStatus());
        fileUtilMockedStatic.verify(() -> FileUtil.writeUsers(users));
    }

    @Test
    void handleUpdateProfile_ShouldUpdateName() throws Exception {
        User currentUser = new User("librarian", "password", "Old Name", "librarian");
        List<User> users = new ArrayList<>(List.of(currentUser));

        currentUserMockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
        fileUtilMockedStatic.when(FileUtil::readUsers).thenReturn(users);

        TextField nameField = (TextField) getField(controller, "profileNameField");
        nameField.setText("New Name");
        PasswordField passField = (PasswordField) getField(controller, "profilePasswordField");
        passField.setText("");

        Method handleUpdateProfileMethod = LibrarianDashboardController.class.getDeclaredMethod("handleUpdateProfile", javafx.event.ActionEvent.class);
        handleUpdateProfileMethod.setAccessible(true);

        try (MockedConstruction<Alert> ignored = mockConstruction(Alert.class, (mock, context) ->
                org.mockito.Mockito.when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK)))) {
            handleUpdateProfileMethod.invoke(controller, (Object) null);
        }

        assertEquals("New Name", currentUser.getFullName());
        fileUtilMockedStatic.verify(() -> FileUtil.writeUsers(users));
    }

    @Test
    void handleUpdateProfile_ShouldUpdateNameAndPassword() throws Exception {
        User currentUser = new User("librarian", "password", "Old Name", "librarian");
        List<User> users = new ArrayList<>(List.of(currentUser));

        currentUserMockedStatic.when(CurrentUser::getCurrentUser).thenReturn(currentUser);
        fileUtilMockedStatic.when(FileUtil::readUsers).thenReturn(users);

        TextField nameField = (TextField) getField(controller, "profileNameField");
        nameField.setText("New Name");
        PasswordField passField = (PasswordField) getField(controller, "profilePasswordField");
        passField.setText("newpass");

        Method handleUpdateProfileMethod = LibrarianDashboardController.class.getDeclaredMethod("handleUpdateProfile", javafx.event.ActionEvent.class);
        handleUpdateProfileMethod.setAccessible(true);

        try (MockedConstruction<Alert> ignored = mockConstruction(Alert.class, (mock, context) ->
                org.mockito.Mockito.when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK)))) {
            handleUpdateProfileMethod.invoke(controller, (Object) null);
        }

        assertEquals("New Name", currentUser.getFullName());
        assertEquals("newpass", currentUser.getPassword());
        fileUtilMockedStatic.verify(() -> FileUtil.writeUsers(users));
    }

    @Test
    void handleUpdateProfile_ShouldNotUpdateWhenNameEmpty() throws Exception {
        TextField nameField = (TextField) getField(controller, "profileNameField");
        nameField.setText("");

        Method handleUpdateProfileMethod = LibrarianDashboardController.class.getDeclaredMethod("handleUpdateProfile", javafx.event.ActionEvent.class);
        handleUpdateProfileMethod.setAccessible(true);

        try (MockedConstruction<Alert> ignored = mockConstruction(Alert.class, (mock, context) ->
                org.mockito.Mockito.when(mock.showAndWait()).thenReturn(Optional.of(ButtonType.OK)))) {
            handleUpdateProfileMethod.invoke(controller, (Object) null);
        }

        fileUtilMockedStatic.verifyNoInteractions();
    }

    private Book createPendingBook() {
        Book book = new Book("Test Title", "Author Name", "Abstract", "Book content");
        book.setAuthorUsername("authorUser");
        book.setStatus("pending");
        return book;
    }

    private void initializeControllerFields() throws Exception {
        setField(controller, "pendingBooksTable", new TableView<Book>());
        setField(controller, "publishedBooksTable", new TableView<Book>());
        setField(controller, "usersTable", new TableView<User>());
        setField(controller, "profileUsernameField", new Label());
        setField(controller, "profileNameField", new TextField());
        setField(controller, "profilePasswordField", new PasswordField());
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Object getField(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }
}

