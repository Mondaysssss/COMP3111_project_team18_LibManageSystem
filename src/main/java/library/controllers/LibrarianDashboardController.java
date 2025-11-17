// src/main/java/library/controllers/LibrarianDashboardController.java
package library.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import library.Main;
import library.models.Book;
import library.models.User;
import library.models.BorrowedBook;
import library.models.Notification;
import library.utils.FileUtil;
import library.utils.CurrentUser;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibrarianDashboardController {

    // Pending Approvals Tab
    @FXML private TableView<Book> pendingBooksTable;
    @FXML private TableColumn<Book, String> pendingTitleColumn;
    @FXML private TableColumn<Book, String> pendingAuthorColumn;
    @FXML private TableColumn<Book, String> pendingAbstractColumn;
    @FXML private TableColumn<Book, Void> pendingActionsColumn;

    // Manage Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, Void> userActionsColumn;
    
    // My Profile Tab
    @FXML private Label profileUsernameField;
    @FXML private TextField profileNameField;
    @FXML private PasswordField profilePasswordField;

    // Borrowed Books Tab
    @FXML private TableView<BorrowedBook> borrowedBooksTable;
    @FXML private TableColumn<BorrowedBook, String> borrowedTitleColumn;
    @FXML private TableColumn<BorrowedBook, String> borrowerColumn;
    @FXML private TableColumn<BorrowedBook, String> borrowDateColumn;
    @FXML private TableColumn<BorrowedBook, String> returnDateColumn;

    // Published Books Tab
    @FXML private TableView<Book> publishedBooksTable;
    @FXML private TableColumn<Book, String> publishedTitleColumn;
    @FXML private TableColumn<Book, String> publishedAuthorColumn;
    @FXML private TableColumn<Book, String> publishedDateColumn;
    @FXML private TableColumn<Book, Integer> borrowCountColumn;
    @FXML private TableColumn<Book, Void> publishedActionsColumn;


    /** Called automatically after FXML is loaded. */
    @FXML
    private void initialize() {
        setupPendingBooksTable();
        loadPendingBooks();
        setupUsersTable();
        loadUsers();
        populateProfileTab();
        setupPublishedBooksTable();
        loadPublishedBooks();
        setupBorrowedBooksTable();
        loadBorrowedBooks();
    }
    
    private void populateProfileTab() {
        User currentUser = CurrentUser.getCurrentUser();
        if (currentUser != null) {
            profileUsernameField.setText(currentUser.getUsername());
            profileNameField.setText(currentUser.getFullName());
        }
    }

    private void setupPendingBooksTable() {
        pendingTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        pendingAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        pendingAbstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractContent"));
        
        pendingActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox pane = new HBox(viewButton, approveButton, rejectButton);

            {
                pane.setSpacing(10);
                viewButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    viewBook(book);
                });
                approveButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    approveBook(book);
                });
                rejectButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    rejectBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadPendingBooks() {
        List<Book> allBooks = FileUtil.readBooks();
        List<Book> pendingBooks = allBooks.stream()
                                          .filter(book -> "pending".equals(book.getStatus()))
                                          .collect(Collectors.toList());
        ObservableList<Book> observableList = FXCollections.observableArrayList(pendingBooks);
        pendingBooksTable.setItems(observableList);
    }

    private void viewBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Content");
        alert.setHeaderText(book.getTitle());
        alert.setContentText(book.getContent());
        alert.showAndWait();
    }

    private void approveBook(Book book) {
        List<Book> allBooks = FileUtil.readBooks();
        for (Book b : allBooks) {
            if (isSameBook(b, book)) {
                b.setStatus("approved");
                if (b.getPublishedDate() == null) {
                    b.setPublishedDate(new Date());
                }
                sendNotificationToAuthor(b, "Your book \"" + b.getTitle() + "\" has been approved.");
                break;
            }
        }
        FileUtil.writeBooks(allBooks);
        loadPendingBooks(); // Refresh the table
    }

    private void rejectBook(Book book) {
        List<Book> allBooks = FileUtil.readBooks();
        Iterator<Book> iterator = allBooks.iterator();
        while (iterator.hasNext()) {
            Book b = iterator.next();
            if (isSameBook(b, book)) {
                iterator.remove();
                sendNotificationToAuthor(b, "Your book \"" + b.getTitle() + "\" has been rejected.");
                break;
            }
        }
        FileUtil.writeBooks(allBooks);
        loadPendingBooks(); // Refresh the table
    }
    
    private void setupPublishedBooksTable() {
        publishedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        publishedAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        borrowCountColumn.setCellValueFactory(new PropertyValueFactory<>("borrowCount"));

        publishedActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(viewButton, deleteButton);

            {
                pane.setSpacing(10);
                viewButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    viewBook(book);
                });
                deleteButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    deleteBook(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadPublishedBooks() {
        List<Book> allBooks = FileUtil.readBooks();
        List<Book> publishedBooks = allBooks.stream()
                                            .filter(book -> "approved".equals(book.getStatus()))
                                            .collect(Collectors.toList());
        ObservableList<Book> observableList = FXCollections.observableArrayList(publishedBooks);
        publishedBooksTable.setItems(observableList);
    }

    private void deleteBook(Book book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this book?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                List<Book> allBooks = FileUtil.readBooks();
                allBooks.removeIf(b -> isSameBook(b, book));
                FileUtil.writeBooks(allBooks);
                loadPublishedBooks();
            }
        });
    }
    
    private void setupBorrowedBooksTable() {
        borrowedTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowerColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerUsername"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
    }

    private void loadBorrowedBooks() {
        List<BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        ObservableList<BorrowedBook> observableList = FXCollections.observableArrayList(borrowedBooks);
        borrowedBooksTable.setItems(observableList);
    }

    private void setupUsersTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().equals("active") ? "true" : "false"
            )
        );

        userActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button();
            private final HBox pane = new HBox(actionButton);

            {
                pane.setSpacing(10);
                actionButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    toggleUserStatus(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    User currentUser = CurrentUser.getCurrentUser();

                    // For the current librarian, the action column should be blank
                    if (currentUser != null && currentUser.getUsername().equals(user.getUsername())) {
                        setGraphic(null);
                    } else {
                        if (user.getStatus().equals("active")) {
                            actionButton.setText("Deactivate");
                        } else {
                            actionButton.setText("Activate");
                        }
                        actionButton.setDisable(false);
                        setGraphic(pane);
                    }
                }
            }
        });
    }

    private void loadUsers() {
        List<User> users = FileUtil.readUsers();
        ObservableList<User> observableList = FXCollections.observableArrayList(users);
        usersTable.setItems(observableList);
    }

    private void toggleUserStatus(User user) {
        List<User> users = FileUtil.readUsers();
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                if ("active".equals(u.getStatus())) {
                    u.setStatus("inactive");
                } else {
                    u.setStatus("active");
                }
                break;
            }
        }
        FileUtil.writeUsers(users);
        loadUsers(); // Refresh the table
    }
    
    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        String newName = profileNameField.getText();
        String newPassword = profilePasswordField.getText();

        if (newName.isEmpty()) {
            showAlert("Error", "Name cannot be empty.");
            return;
        }

        User currentUser = CurrentUser.getCurrentUser();
        List<User> users = FileUtil.readUsers();
        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUsername())) {
                user.setFullName(newName);
                if (!newPassword.isEmpty()) {
                    user.setPassword(newPassword);
                }
                break;
            }
        }
        FileUtil.writeUsers(users);
        showAlert("Success", "Profile updated successfully.");
    }

    /** Log out back to the Home screen. */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
            Stage st = Main.getPrimaryStage();
            st.setScene(new Scene(root, 640, 480));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void sendNotificationToAuthor(Book book, String message) {
        if (book.getAuthorUsername() == null || book.getAuthorUsername().isBlank()) {
            return;
        }
        List<Notification> notifications = FileUtil.readNotifications();
        notifications.add(new Notification(message, book.getAuthorUsername()));
        FileUtil.writeNotifications(notifications);
    }

    private boolean isSameBook(Book left, Book right) {
        if (left == null || right == null) {
            return false;
        }
        return Objects.equals(left.getTitle(), right.getTitle()) &&
               Objects.equals(left.getAuthorUsername(), right.getAuthorUsername()) &&
               Objects.equals(left.getAbstractContent(), right.getAbstractContent());
    }
}