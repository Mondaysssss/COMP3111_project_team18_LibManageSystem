package library.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import library.Main;
import library.models.User;
import library.utils.FileUtil;
import library.utils.CurrentUser;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoginController {
    @FXML private Label headerLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private String selectedRole;

    public void setRole(String role) {
        this.selectedRole = role;
        String role_name = switch (role.toLowerCase()) {
            case "student", "staff" -> "Student/Staff";
            case "author" -> "Author";
            case "librarian" -> "Librarian";
            default -> "";
        };
        headerLabel.setText(role_name + " Login");
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent home = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Home.fxml")));
        Main.getPrimaryStage().setScene(new Scene(home, 640, 480));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and password are required.");
            return;
        }

        List<User> users = FileUtil.readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.getRole().equalsIgnoreCase(selectedRole)) {
                if (user.getStatus().equals("active")) {
                    CurrentUser.setCurrentUser(user);
                    navigateToDashboard();
                } else {
                    showAlert("Error", "User is not active.");
                }
                return;
            }
        }

        showAlert("Error", "Invalid username or password.");
    }

    private void navigateToDashboard() {
        String fxml;
        switch (selectedRole.toLowerCase()) {
            case "student":   fxml = "/fxml/StudentDashboard.fxml";   break;
            case "author":    fxml = "/fxml/AuthorDashboard.fxml";    break;
            case "librarian": fxml = "/fxml/LibrarianDashboard.fxml"; break;
            default:          fxml = "/fxml/Home.fxml";               break;
        }
        try {
            Parent dash = FXMLLoader.load(getClass().getResource(fxml));
            Main.getPrimaryStage().setScene(new Scene(dash, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** New: navigate to the standalone Register screen */
    @FXML
    private void handleGoToRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
        Parent root = loader.load();
        RegisterController ctrl = loader.getController();
        ctrl.setRole(selectedRole);
        Main.getPrimaryStage().setScene(new Scene(root, 640, 480));
    }



    private String capitalize(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
