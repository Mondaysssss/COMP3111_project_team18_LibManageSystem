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

public class StudentDashboardController {

    // ===== Task 1.2: Available Books UI 元件 =====
    @FXML private TableView<Book> availableBooksTable; // task1.2
    @FXML private TableColumn<Book, String> colAvailableTitle; // task1.2
    @FXML private TableColumn<Book, String> colAvailableAuthor; // task1.2
    @FXML private TableColumn<Book, Date> colAvailablePublished; // task1.2
    @FXML private TableColumn<Book, String> colAvailableAbstract; // task1.2
    @FXML private Label detailTitleLabel; // task1.2
    @FXML private Label detailAuthorLabel; // task1.2
    @FXML private Label detailPublishedLabel; // task1.2
    @FXML private TextArea detailAbstractArea; // task1.2

    // ===== Task 1.3: Borrowed Books UI 元件 =====
    @FXML private TableView<BorrowedBook> borrowedBooksTable; // task1.3
    @FXML private TableColumn<BorrowedBook, String> colBorrowedTitle; // task1.3
    @FXML private TableColumn<BorrowedBook, String> colBorrowedAuthor; // task1.3 (由 book 反查作者)
    @FXML private TableColumn<BorrowedBook, Date> colBorrowedDate; // task1.3
    @FXML private TableColumn<BorrowedBook, String> colBorrowedTimeout; // task1.3

    // ===== Task 1.4: Profile UI 元件 =====
    @FXML private Label profileUsernameLabel; // task1.4
    @FXML private TextField profileNewNameField; // task1.4
    @FXML private PasswordField profileNewPasswordField; // task1.4

    // ===== Task 1.5: Notification board UI 元件 =====
    @FXML private ListView<String> notificationListView; // task1.5

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Timeline borrowedTimeLine; // 用於定時刷新「Time Left」欄位

    /** Called automatically after FXML is loaded. */
    @FXML
    private void initialize() {
        User current = CurrentUser.getCurrentUser();
        if (current == null) {
            // 若沒有登入使用者，保險起見退回首頁
            navigateHome();
            return;
        }

        // Task 1.2: 初始化可借書列表
        initAvailableBooksTable(); // task1.2

        // Task 1.3: 初始化已借書列表
        initBorrowedBooksTable(); // task1.3

        // Task 1.4: 初始化個人資料畫面
        initProfileTab(); // task1.4

        // Task 1.5: 初始化通知板
        loadNotifications(); // task1.5
    }

    // ===== Task 1.2: Available book screen =====

    private void initAvailableBooksTable() { // task1.2
        if (availableBooksTable == null) {
            return; // FXML 尚未載入對應 tab 時避免 NPE
        }

        colAvailableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAvailableAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAvailablePublished.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        colAvailableAbstract.setCellValueFactory(new PropertyValueFactory<>("abstractContent"));

        refreshAvailableBooks(); // task1.2

        availableBooksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showBookDetails(newVal);
            }
        });
    }

    private void refreshAvailableBooks() { // task1.2
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

    private void showBookDetails(Book book) { // task1.2
        detailTitleLabel.setText(book.getTitle());
        detailAuthorLabel.setText(book.getAuthor());
        Date pub = book.getPublishedDate();
        detailPublishedLabel.setText(pub != null ? dateFormat.format(pub) : "N/A");
        detailAbstractArea.setText(book.getAbstractContent());
    }

    // Task 1.2: Borrow Selected Book 流程
    @FXML
    private void handleBorrowSelectedBook(ActionEvent event) { // task1.2
        Book selected = availableBooksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a book to borrow.");
            return;
        }

        // 彈出輸入借閱時間的對話框
        Dialog<int[]> dialog = createDurationDialog(); // task1.2
        Optional<int[]> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        int[] duration = result.get();
        int minutes = duration[0];
        int seconds = duration[1];

        long totalMinutes = minutes + seconds / 60;
        if (totalMinutes <= 0) {
            showAlert("Error", "Borrow duration must be greater than 0.");
            return;
        }
        if (totalMinutes > 20160) { // 14 days * 24 * 60
            showAlert("Error", "Borrow duration cannot exceed 20160 minutes (14 days).");
            return;
        }

        Date now = new Date();
        long millis = java.time.Duration.ofMinutes(totalMinutes).toMillis();
        Date returnDate = new Date(now.getTime() + millis);

        List<BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        BorrowedBook record = new BorrowedBook(selected.getTitle(),
                CurrentUser.getCurrentUser().getUsername(),
                now,
                returnDate);
        borrowedBooks.add(record);
        FileUtil.writeBorrowedBooks(borrowedBooks);

        // 更新書的借閱數
        List<Book> allBooks = FileUtil.readBooks();
        for (Book book : allBooks) {
            if (book.getTitle().equals(selected.getTitle())) {
                book.setBorrowCount(book.getBorrowCount() + 1);
                break;
            }
        }
        FileUtil.writeBooks(allBooks);

        // 從可借列表移除，並刷新已借書列表
        refreshAvailableBooks();
        refreshBorrowedBooks();

        showAlert("Hint", "Book borrowed successfully.");
    }

    private Dialog<int[]> createDurationDialog() { // task1.2
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

    // ===== Task 1.3: Borrowed book screen =====

    private void initBorrowedBooksTable() { // task1.3
        if (borrowedBooksTable == null) {
            return;
        }

        colBorrowedTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowedDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        // 作者名稱透過 bookTitle 再查 Book 列表
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
        // 啟動定時刷新，使 Time Left 在 UI 上持續更新
        startBorrowedTimer();

        refreshBorrowedBooks(); // task1.3
    }

    private void refreshBorrowedBooks() { // task1.3
        if (borrowedBooksTable == null) {
            return;
        }
        String username = CurrentUser.getCurrentUser().getUsername();
        List<BorrowedBook> all = FileUtil.readBorrowedBooks();

        // Task 1.3（修改需求）：借閱逾期只發通知，不自動歸還、不讓書回到 Available
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

    private String formatTimeLeft(Date returnDate) { // task1.3
        long diff = returnDate.getTime() - System.currentTimeMillis();
        if (diff <= 0) {
            return "Expired";
        }
        long seconds = diff / 1000;
        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
    }

    /**
     * 啟動一個 Timeline，每秒刷新一次借閱表，使「Time Left」欄位在 UI 中即時更新。
     */
    private void startBorrowedTimer() { // task1.3
        if (borrowedBooksTable == null) {
            return;
        }
        if (borrowedTimeLine != null) {
            borrowedTimeLine.stop();
        }
        borrowedTimeLine = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // 只需要刷新 TableView，cellValueFactory 會重新計算 Time Left
            borrowedBooksTable.refresh();
        }));
        borrowedTimeLine.setCycleCount(Timeline.INDEFINITE);
        borrowedTimeLine.play();
    }

    // Task 1.3: Read selected book
    @FXML
    private void handleReadSelectedBook(ActionEvent event) { // task1.3
        BorrowedBook selected = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a borrowed book to read.");
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

    // Task 1.3: Return selected book
    @FXML
    private void handleReturnSelectedBook(ActionEvent event) { // task1.3
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

        // 新增通知
        List<Notification> notifications = FileUtil.readNotifications();
        String msg = "The book \"" + selected.getBookTitle() + "\" has been returned.";
        notifications.add(new Notification(msg, selected.getBorrowerUsername()));
        FileUtil.writeNotifications(notifications);

        refreshBorrowedBooks();
        refreshAvailableBooks();

        showAlert("Hint", "Book returned successfully.");
    }

    // ===== Task 1.4: Manage profile screen =====

    private void initProfileTab() { // task1.4
        if (profileUsernameLabel == null) {
            return;
        }
        User current = CurrentUser.getCurrentUser();
        profileUsernameLabel.setText(current.getUsername());
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) { // task1.4
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

        showAlert("Hint", "Account updated successfully."); // task1.4
    }

    // ===== Task 1.5: Notification board =====

    private void loadNotifications() { // task1.5
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
    private void handleClearSelectedNotification(ActionEvent event) { // task1.5
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
    private void handleClearAllNotifications(ActionEvent event) { // task1.5
        String username = CurrentUser.getCurrentUser().getUsername();
        List<Notification> notifications = FileUtil.readNotifications();
        notifications.removeIf(n -> username.equals(n.getAuthorUsername()));
        FileUtil.writeNotifications(notifications);

        loadNotifications();
    }

    // ===== 共用工具方法 =====

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
