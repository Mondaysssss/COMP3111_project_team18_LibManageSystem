package library.controllers;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import library.Main;
import library.models.Book;
import library.models.BorrowedBook;
import library.models.Notification;
import library.models.User;
import library.utils.CurrentUser;
import library.utils.FileUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AuthorDashboardController {

    @FXML private TabPane tabPane;
    
    // My Books Tab
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> statusColumn;
    @FXML private TableColumn<Book, String> publishDateColumn;
    @FXML private TableColumn<Book, String> readersColumn;
    @FXML private TableColumn<Book, String> abstractColumn;
    @FXML private Button viewButton;
    @FXML private Button modifyButton;
    @FXML private Button deleteButton;
    
    // Publish New Book Tab
    @FXML private TextField bookTitleField;
    @FXML private TextField selectedFileField;
    @FXML private TextArea summaryField;
    @FXML private Button chooseFileButton;
    @FXML private Button publishButton;
    @FXML private Button generateButton;
    
    // Status View Tab
    @FXML private javafx.scene.chart.PieChart statusPieChart;
    @FXML private javafx.scene.chart.BarChart<String, Number> popularityBarChart;
    @FXML private Button refreshStatsButton;
    
    // My Profile Tab
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private PasswordField newPasswordField;
    @FXML private Button updateProfileButton;
    
    // Inform Board Tab
    @FXML private VBox notificationsVBox;
    @FXML private Button clearAllButton;
    
    private File selectedBookFile;
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = CurrentUser.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        setupMyBooksTab();
        setupPublishNewBookTab();
        setupStatusViewTab();
        setupMyProfileTab();
        setupInformBoardTab();
    }

    private void setupMyBooksTab() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        publishDateColumn.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            if (book.getPublishedDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new SimpleStringProperty(sdf.format(book.getPublishedDate()));
            }
            return new SimpleStringProperty("");
        });
        readersColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getReaders())));
        abstractColumn.setCellValueFactory(new PropertyValueFactory<>("abstractContent"));
        
        refreshBooksTable();
    }

    private void refreshBooksTable() {
        List<Book> allBooks = FileUtil.readBooks();
        List<Book> authorBooks = allBooks.stream()
            .filter(book -> book.getAuthorUsername() != null && 
                           book.getAuthorUsername().equals(currentUser.getUsername()))
            .collect(Collectors.toList());
        
        ObservableList<Book> books = FXCollections.observableArrayList(authorBooks);
        booksTable.setItems(books);
    }

    @FXML
    private void handleView(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to view.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BookReader.fxml"));
            Parent root = loader.load();
            BookReaderController controller = loader.getController();
            controller.setBook(selectedBook);
            
            Stage stage = new Stage();
            stage.setTitle("Reading: " + selectedBook.getTitle());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open book reader.");
        }
    }

    @FXML
    private void handleModify(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to modify.");
            return;
        }
        
        // Check if book can be modified
        if ("approved".equalsIgnoreCase(selectedBook.getStatus()) && isBookCurrentlyBorrowed(selectedBook)) {
            showAlert("Error", "Cannot modify book that is currently borrowed.");
            return;
        }
        
        // Show modify dialog
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit fields you want to change");
        dialog.setHeaderText(null);
        
        TextField titleField = new TextField(selectedBook.getTitle());
        TextArea summaryField = new TextArea(selectedBook.getAbstractContent());
        summaryField.setPrefRowCount(5);
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Title:"), titleField,
            new Label("Summary:"), summaryField
        );
        dialog.getDialogPane().setContent(content);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Map<String, String> result = new HashMap<>();
                result.put("title", titleField.getText());
                result.put("summary", summaryField.getText());
                return result;
            }
            return null;
        });
        
        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String originalTitle = selectedBook.getTitle();
            selectedBook.setTitle(data.get("title"));
            selectedBook.setAbstractContent(data.get("summary"));
            
            // If book was approved, set status back to pending for re-approval
            if (selectedBook.getStatus().equals("approved")) {
                selectedBook.setStatus("pending");
            }
            
            List<Book> books = FileUtil.readBooks();
            for (int i = 0; i < books.size(); i++) {
                // Use original title to find the book before it was modified
                if (books.get(i).getTitle().equals(originalTitle) &&
                    books.get(i).getAuthorUsername() != null &&
                    books.get(i).getAuthorUsername().equals(selectedBook.getAuthorUsername())) {
                    books.set(i, selectedBook);
                    break;
                }
            }
            FileUtil.writeBooks(books);
            refreshBooksTable();
            
            showAlert("Hint", "Book updated. Please waiting for approval.");
        });
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to delete.");
            return;
        }
        
        // Check if book can be deleted
        if ("approved".equalsIgnoreCase(selectedBook.getStatus()) && isBookCurrentlyBorrowed(selectedBook)) {
            showAlert("Error", "Cannot delete book that is currently borrowed.");
            return;
        }
        
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Delete \"" + selectedBook.getTitle() + "\"?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<Book> books = FileUtil.readBooks();
            books.removeIf(book -> book.getTitle().equals(selectedBook.getTitle()) &&
                                 book.getAuthorUsername().equals(selectedBook.getAuthorUsername()));
            FileUtil.writeBooks(books);
            refreshBooksTable();
        }
    }

    private void setupPublishNewBookTab() {
        selectedFileField.setEditable(false);
    }

    @FXML
    private void handleChooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Text File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        selectedBookFile = fileChooser.showOpenDialog(Main.getPrimaryStage());
        if (selectedBookFile != null) {
            selectedFileField.setText(selectedBookFile.getName());
        }
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        // This is for H-Task only - placeholder implementation
        if (selectedBookFile != null) {
            try {
                String content = FileUtils.readFileToString(selectedBookFile, StandardCharsets.UTF_8);
                // Simple summary generation - take first 200 characters
                String summary = content.length() > 200 ? content.substring(0, 200) + "..." : content;
                summaryField.setText(summary);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to read file.");
            }
        } else {
            showAlert("Error", "Please select a text file first.");
        }
    }

    @FXML
    private void handlePublishBook(ActionEvent event) {
        String title = bookTitleField.getText().trim();
        String summary = summaryField.getText().trim();
        
        if (title.isEmpty() || summary.isEmpty()) {
            showAlert("Error", "Book title and summary are required.");
            return;
        }
        
        if (selectedBookFile == null) {
            showAlert("Error", "Please select a text file.");
            return;
        }
        
        try {
            String content = FileUtils.readFileToString(selectedBookFile, StandardCharsets.UTF_8);
            Book newBook = new Book(title, currentUser.getFullName(), summary, content);
            newBook.setAuthorUsername(currentUser.getUsername());
            
            List<Book> books = FileUtil.readBooks();
            books.add(newBook);
            FileUtil.writeBooks(books);
            
            // Clear fields
            bookTitleField.clear();
            selectedFileField.clear();
            summaryField.clear();
            selectedBookFile = null;
            
            refreshBooksTable();
            showAlert("Hint", "Published and awaiting approval.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to read file.");
        }
    }

    private void setupStatusViewTab() {
        refreshStats();
    }

    @FXML
    private void handleRefreshStats(ActionEvent event) {
        refreshStats();
    }

    private void refreshStats() {
        List<Book> allBooks = FileUtil.readBooks();
        List<Book> authorBooks = allBooks.stream()
            .filter(book -> book.getAuthorUsername() != null && 
                           book.getAuthorUsername().equals(currentUser.getUsername()))
            .collect(Collectors.toList());
        
        // Update pie chart
        long pendingCount = authorBooks.stream()
            .filter(book -> "pending".equals(book.getStatus()))
            .count();
        long approvedCount = authorBooks.stream()
            .filter(book -> "approved".equals(book.getStatus()))
            .count();
        
        ObservableList<javafx.scene.chart.PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new javafx.scene.chart.PieChart.Data("Pending", pendingCount),
            new javafx.scene.chart.PieChart.Data("Approved", approvedCount)
        );
        statusPieChart.setData(pieChartData);
        statusPieChart.setTitle("My Books: Status");
        
        // Update bar chart - Top 5 most popular books
        List<Book> topBooks = authorBooks.stream()
            .sorted((b1, b2) -> Integer.compare(b2.getBorrowCount(), b1.getBorrowCount()))
            .limit(5)
            .collect(Collectors.toList());
        
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        series.setName("Borrow Count");
        
        for (Book book : topBooks) {
            String shortTitle = book.getTitle().length() > 30 ? 
                book.getTitle().substring(0, 30) + "..." : book.getTitle();
            series.getData().add(new javafx.scene.chart.XYChart.Data<>(shortTitle, book.getBorrowCount()));
        }
        
        popularityBarChart.getData().clear();
        if (!topBooks.isEmpty()) {
            popularityBarChart.getData().add(series);
        }
        popularityBarChart.setTitle("Top 5 Most Popular Books");
    }

    private void setupMyProfileTab() {
        usernameField.setText(currentUser.getUsername());
        usernameField.setEditable(false);
        fullNameField.setText(currentUser.getFullName());
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        String newName = fullNameField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        
        if (newName.isEmpty()) {
            showAlert("Error", "Full name cannot be empty.");
            return;
        }
        
        List<User> users = FileUtil.readUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(currentUser.getUsername())) {
                users.get(i).setFullName(newName);
                if (!newPassword.isEmpty()) {
                    users.get(i).setPassword(newPassword);
                }
                FileUtil.writeUsers(users);
                currentUser = users.get(i);
                CurrentUser.setCurrentUser(currentUser);
                newPasswordField.clear();
                showAlert("Hint", "Account updated successfully.");
                return;
            }
        }
    }

    private void setupInformBoardTab() {
        refreshNotifications();
    }

    private void refreshNotifications() {
        notificationsVBox.getChildren().clear();
        
        List<Notification> allNotifications = FileUtil.readNotifications();
        List<Notification> authorNotifications = allNotifications.stream()
            .filter(n -> n.getAuthorUsername().equals(currentUser.getUsername()))
            .collect(Collectors.toList());
        
        for (Notification notification : authorNotifications) {
            HBox notificationBox = new HBox(10);
            Label messageLabel = new Label(notification.getMessage());
            messageLabel.setWrapText(true);
            messageLabel.setPrefWidth(600);
            
            Button clearButton = new Button("Clear");
            clearButton.setOnAction(e -> {
                allNotifications.remove(notification);
                FileUtil.writeNotifications(allNotifications);
                refreshNotifications();
            });
            
            notificationBox.getChildren().addAll(messageLabel, clearButton);
            notificationsVBox.getChildren().add(notificationBox);
        }
    }

    @FXML
    private void handleClearAll(ActionEvent event) {
        List<Notification> allNotifications = FileUtil.readNotifications();
        allNotifications.removeIf(n -> n.getAuthorUsername().equals(currentUser.getUsername()));
        FileUtil.writeNotifications(allNotifications);
        refreshNotifications();
    }

    private boolean isBookCurrentlyBorrowed(Book book) {
        List<BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        return borrowedBooks.stream()
            .anyMatch(bb -> bb.getBookTitle().equals(book.getTitle()));
    }

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
}
