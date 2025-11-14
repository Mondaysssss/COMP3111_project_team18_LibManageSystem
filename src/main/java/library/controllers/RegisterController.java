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
import library.services.UserService;

import java.io.IOException;

public class RegisterController {

    @FXML private Label headerLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField fullNameField;

    private String selectedRole;
    private UserService userService;

    public RegisterController() {
        userService = UserService.getInstance();
    }

    public void setRole(String role) {
        this.selectedRole = role;
        String roleName = switch (role.toLowerCase()) {
            case "student", "staff" -> "Student/Staff";
            case "author" -> "Author";
            case "librarian" -> "Librarian";
            default -> capitalize(role);
        };
        headerLabel.setText(roleName + " Register");
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent home = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
        Main.getPrimaryStage().setScene(new Scene(home, 640, 480));
    }

    @FXML
    private void handleGoToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        LoginController ctrl = loader.getController();
        ctrl.setRole(selectedRole);
        Main.getPrimaryStage().setScene(new Scene(root, 640, 480));
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String fullName = fullNameField.getText();
        
        // Validate input fields
        if (username == null || username.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please enter a username.");
            return;
        }
        
        if (password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please enter a password.");
            return;
        }
        
        if (password.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", 
                    "Password must be at least 4 characters long.");
            passwordField.clear();
            confirmPasswordField.clear();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", 
                    "Passwords do not match. Please try again.");
            passwordField.clear();
            confirmPasswordField.clear();
            return;
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please enter your full name.");
            return;
        }
        
        // Check if username already exists
        if (userService.usernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", 
                    "Username already exists. Please choose a different username.");
            usernameField.clear();
            return;
        }
        
        // Register the user
        boolean success = userService.registerUser(username, password, fullName, selectedRole);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", 
                    "Account created successfully! You can now login.");
            
            // Navigate to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController ctrl = loader.getController();
            ctrl.setRole(selectedRole);
            Main.getPrimaryStage().setScene(new Scene(root, 640, 480));
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", 
                    "Failed to register. Please try again.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
