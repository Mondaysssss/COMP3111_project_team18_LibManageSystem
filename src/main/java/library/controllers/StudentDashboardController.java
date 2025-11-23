package library.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import library.Main;
import library.models.Book;
import library.models.BorrowedBook;
import library.models.Notification;
import library.models.User;
import library.utils.CurrentUser;
import library.utils.FileUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Student/Staff Dashboard.
 * 
 * <p>This controller manages the main interface for students and staff members,
 * providing functionality to:
 * <ul>
 *   <li>Browse and view available books</li>
 *   <li>Borrow books with custom duration (up to 14 days)</li>
 *   <li>View and manage borrowed books</li>
 *   <li>Read borrowed books (if not expired)</li>
 *   <li>Return borrowed books</li>
 *   <li>Update profile information (name and password)</li>
 *   <li>View and manage notifications</li>
 * </ul>
 * 
 * <p>The dashboard automatically refreshes borrowed book timeouts every second
 * and creates notifications for expired books. It filters available books to
 * exclude those currently borrowed by any user.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class StudentDashboardController {

    @FXML private TableView<Book> availableBooksTable;
    @FXML private TableColumn<Book, String> colAvailableTitle;
    @FXML private TableColumn<Book, String> colAvailableAuthor;
    @FXML private TableColumn<Book, Date> colAvailablePublished;
    @FXML private TableColumn<Book, String> colAvailableAbstract;
    @FXML private Label detailTitleLabel;
    @FXML private Label detailAuthorLabel;
    @FXML private Label detailPublishedLabel;
    @FXML private TextArea detailAbstractArea;

    @FXML private TableView<BorrowedBook> borrowedBooksTable;
    @FXML private TableColumn<BorrowedBook, String> colBorrowedTitle;
    @FXML private TableColumn<BorrowedBook, String> colBorrowedAuthor;
    @FXML private TableColumn<BorrowedBook, Date> colBorrowedDate;
    @FXML private TableColumn<BorrowedBook, String> colBorrowedTimeout;

    @FXML private Label profileUsernameLabel;
    @FXML private TextField profileNewNameField;
    @FXML private PasswordField profileNewPasswordField;

    @FXML private ListView<String> notificationListView;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Timeline borrowedTimeLine;

    /**
     * Initializes the dashboard after FXML is loaded.
     * Sets up all tables, profile information, and notifications.
     * Navigates to home if no user is logged in.
     */
    @FXML
    private void initialize() {
        User current = CurrentUser.getCurrentUser();
        if (current == null) {
            navigateHome();
            return;
        }

        initAvailableBooksTable();

        initBorrowedBooksTable();

        initProfileTab();

        loadNotifications();
    }

    private void initAvailableBooksTable() {
        if (availableBooksTable == null) {
            return;
        }

        colAvailableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAvailableAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAvailablePublished.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        colAvailableAbstract.setCellValueFactory(new PropertyValueFactory<>("abstractContent"));

        refreshAvailableBooks();

        availableBooksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showBookDetails(newVal);
            }
        });
    }

    private void refreshAvailableBooks() {
        List<Book> allBooks = FileUtil.readBooks();
        List<BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        Set<String> borrowedTitles = borrowedBooks.stream()
                .map(BorrowedBook::getBookTitle)
                .collect(Collectors.toSet());

        List<Book> available = allBooks.stream()
                .filter(book -> !borrowedTitles.contains(book.getTitle()))
                .collect(Collectors.toList());

        ObservableList<Book> observableBooks = FXCollections.observableArrayList(available);
        availableBooksTable.setItems(observableBooks);
    }

    private void showBookDetails(Book book) {
        detailTitleLabel.setText(book.getTitle());
        detailAuthorLabel.setText(book.getAuthor());
        Date pub = book.getPublishedDate();
        detailPublishedLabel.setText(pub != null ? dateFormat.format(pub) : "N/A");
        detailAbstractArea.setText(book.getAbstractContent());
    }

    @FXML
    private void handleBorrowSelectedBook(ActionEvent event) {
        Book selected = availableBooksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a book to borrow.");
            return;
        }

        Dialog<int[]> dialog = createDurationDialog();
        Optional<int[]> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        int[] duration = result.get();
        int minutes = duration[0];
        int seconds = duration[1];

        long totalSeconds = minutes * 60L + seconds;
        if (totalSeconds <= 0) {
            showAlert("Error", "Borrow duration must be at least 1 second.");
            return;
        }
        long maxSeconds = 20160L * 60; // 14 days
        if (totalSeconds > maxSeconds) {
            showAlert("Error", "Borrow duration cannot exceed 20160 minutes (14 days).");
            return;
        }

        Date now = new Date();
        long millis = java.time.Duration.ofSeconds(totalSeconds).toMillis();
        Date returnDate = new Date(now.getTime() + millis);

        List<BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        BorrowedBook record = new BorrowedBook(selected.getTitle(),
                CurrentUser.getCurrentUser().getUsername(),
                now,
                returnDate);
        borrowedBooks.add(record);
        FileUtil.writeBorrowedBooks(borrowedBooks);

        List<Book> allBooks = FileUtil.readBooks();
        for (Book book : allBooks) {
            if (book.getTitle().equals(selected.getTitle())) {
                book.setBorrowCount(book.getBorrowCount() + 1);
                break;
            }
        }
        FileUtil.writeBooks(allBooks);

        refreshAvailableBooks();
        refreshBorrowedBooks();

        showAlert("Hint", "Book borrowed successfully.");
    }

    private Dialog<int[]> createDurationDialog() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.setTitle("Borrow Duration");
        dialog.setHeaderText("Please enter the borrowing duration (minutes and seconds).");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        TextField minutesField = new TextField("0");
        TextField secondsField = new TextField("0");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Minutes:"), 0, 0);
        grid.add(minutesField, 1, 0);
        grid.add(new Label("Seconds:"), 0, 1);
        grid.add(secondsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                int m = parseIntSafe(minutesField.getText());
                int s = parseIntSafe(secondsField.getText());
                return new int[]{m, s};
            }
            return null;
        });

        return dialog;
    }

    private void initBorrowedBooksTable() {
        if (borrowedBooksTable == null) {
            return;
        }

        colBorrowedTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowedDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        colBorrowedAuthor.setCellValueFactory(cellData -> {
            String title = cellData.getValue().getBookTitle();
            List<Book> books = FileUtil.readBooks();
            String author = books.stream()
                    .filter(b -> b.getTitle().equals(title))
                    .map(Book::getAuthor)
                    .findFirst()
                    .orElse("N/A");
            return new javafx.beans.property.SimpleStringProperty(author);
        });

        colBorrowedTimeout.setCellValueFactory(cellData -> {
            BorrowedBook bb = cellData.getValue();
            String text = formatTimeLeft(bb.getReturnDate());
            return new javafx.beans.property.SimpleStringProperty(text);
        });
        startBorrowedTimer();

        refreshBorrowedBooks();
    }

    private void refreshBorrowedBooks() {
        if (borrowedBooksTable == null) {
            return;
        }
        String username = CurrentUser.getCurrentUser().getUsername();
        List<BorrowedBook> all = FileUtil.readBorrowedBooks();

        List<Notification> notifications = FileUtil.readNotifications();
        boolean notificationUpdated = false;
        Date now = new Date();
        for (BorrowedBook bb : all) {
            if (bb.getReturnDate().before(now)) {
                String msg = "The book \"" + bb.getBookTitle() + "\" you borrowed has expired.";
                boolean exists = notifications.stream()
                        .anyMatch(n -> username.equals(n.getAuthorUsername()) && msg.equals(n.getMessage()));
                if (!exists) {
                    notifications.add(new Notification(msg, bb.getBorrowerUsername()));
                    notificationUpdated = true;
                }
            }
        }
        if (notificationUpdated) {
            FileUtil.writeNotifications(notifications);
        }

        List<BorrowedBook> mine = all.stream()
                .filter(bb -> username.equals(bb.getBorrowerUsername()))
                .collect(Collectors.toList());
        borrowedBooksTable.setItems(FXCollections.observableArrayList(mine));
    }

    private String formatTimeLeft(Date returnDate) {
        long diff = returnDate.getTime() - System.currentTimeMillis();
        if (diff <= 0) {
            return "Expired";
        }
        java.time.Duration duration = java.time.Duration.ofMillis(diff);
        long days = duration.toDays();
        duration = duration.minusDays(days);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.getSeconds();
        return String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
    }

    private void startBorrowedTimer() {
        if (borrowedBooksTable == null) {
            return;
        }
        if (borrowedTimeLine != null) {
            borrowedTimeLine.stop();
        }
        borrowedTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            borrowedBooksTable.refresh();
        }));
        borrowedTimeLine.setCycleCount(Timeline.INDEFINITE);
        borrowedTimeLine.play();
    }

    @FXML
    private void handleReadSelectedBook(ActionEvent event) {
        BorrowedBook selected = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a borrowed book to read.");
            return;
        }

        // 若已過期則禁止閱讀
        if (selected.getReturnDate().before(new Date())) {
            showAlert("Error", "This book has expired. Please return it before reading.");
            return;
        }

        List<Book> books = FileUtil.readBooks();
        Book target = books.stream()
                .filter(b -> b.getTitle().equals(selected.getBookTitle()))
                .findFirst()
                .orElse(null);
        if (target == null) {
            showAlert("Error", "Book content not found.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BookReader.fxml"));
            Parent root = loader.load();
            BookReaderController controller = loader.getController();
            controller.setBook(target);

            Stage stage = new Stage();
            stage.setTitle("Reading: " + target.getTitle());
            stage.initOwner(Main.getPrimaryStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open book reader.");
        }
    }

    @FXML
    private void handleReturnSelectedBook(ActionEvent event) {
        BorrowedBook selected = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a borrowed book to return.");
            return;
        }

        List<BorrowedBook> all = FileUtil.readBorrowedBooks();
        all.removeIf(bb -> bb.getBookTitle().equals(selected.getBookTitle())
                && bb.getBorrowerUsername().equals(selected.getBorrowerUsername())
                && bb.getBorrowDate().equals(selected.getBorrowDate()));
        FileUtil.writeBorrowedBooks(all);

        List<Notification> notifications = FileUtil.readNotifications();
        String msg = "The book \"" + selected.getBookTitle() + "\" has been returned.";
        notifications.add(new Notification(msg, selected.getBorrowerUsername()));
        FileUtil.writeNotifications(notifications);

        refreshBorrowedBooks();
        refreshAvailableBooks();

        showAlert("Hint", "Book returned successfully.");
    }

    private void initProfileTab() {
        if (profileUsernameLabel == null) {
            return;
        }
        User current = CurrentUser.getCurrentUser();
        profileUsernameLabel.setText(current.getUsername());
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        User current = CurrentUser.getCurrentUser();
        if (current == null) {
            showAlert("Error", "No user logged in.");
            return;
        }

        String newName = profileNewNameField.getText().trim();
        String newPassword = profileNewPasswordField.getText().trim();

        if (newName.isEmpty() && newPassword.isEmpty()) {
            showAlert("Error", "Please enter a new name or a new password.");
            return;
        }

        List<User> users = FileUtil.readUsers();
        for (User user : users) {
            if (user.getUsername().equals(current.getUsername())) {
                if (!newName.isEmpty()) {
                    user.setFullName(newName);
                }
                if (!newPassword.isEmpty()) {
                    user.setPassword(newPassword);
                }
                CurrentUser.setCurrentUser(user);
                break;
            }
        }
        FileUtil.writeUsers(users);

        profileNewNameField.clear();
        profileNewPasswordField.clear();

        showAlert("Hint", "Account updated successfully.");
    }

    private void loadNotifications() {
        if (notificationListView == null) {
            return;
        }
        String username = CurrentUser.getCurrentUser().getUsername();
        List<Notification> notifications = FileUtil.readNotifications();

        List<String> messages = notifications.stream()
                .filter(n -> username.equals(n.getAuthorUsername()))
                .map(Notification::getMessage)
                .collect(Collectors.toList());

        notificationListView.setItems(FXCollections.observableArrayList(messages));
    }

    @FXML
    private void handleClearSelectedNotification(ActionEvent event) {
        int index = notificationListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            showAlert("Error", "Please select a notification to clear.");
            return;
        }

        String username = CurrentUser.getCurrentUser().getUsername();
        List<Notification> notifications = FileUtil.readNotifications();
        List<Notification> mine = notifications.stream()
                .filter(n -> username.equals(n.getAuthorUsername()))
                .collect(Collectors.toList());
        if (index >= mine.size()) {
            return;
        }
        Notification toRemove = mine.get(index);
        notifications.remove(toRemove);
        FileUtil.writeNotifications(notifications);

        loadNotifications();
    }

    @FXML
    private void handleClearAllNotifications(ActionEvent event) {
        String username = CurrentUser.getCurrentUser().getUsername();
        List<Notification> notifications = FileUtil.readNotifications();
        notifications.removeIf(n -> username.equals(n.getAuthorUsername()));
        FileUtil.writeNotifications(notifications);

        loadNotifications();
    }

    private void navigateHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
            Stage st = Main.getPrimaryStage();
            st.setScene(new Scene(root, 640, 480));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Log out back to the Home screen. */
    @FXML
    private void handleLogout(ActionEvent event) {
        navigateHome();
    }

    private int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
