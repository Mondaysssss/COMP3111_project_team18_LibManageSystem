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

import java.io.IOException;
import java.util.List;

public class RegisterController {

    @FXML private Label headerLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;

    private String selectedRole;

    public void setRole(String role) {
        this.selectedRole = role;
        headerLabel.setText(capitalize(role) + " Register");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String fullName = fullNameField.getText();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        List<User> users = FileUtil.readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                showAlert("Error", "Username already exists.");
                return;
            }
        }

        User newUser = new User(username, password, fullName, selectedRole);
        users.add(newUser);
        FileUtil.writeUsers(users);

        showAlert("Success", "Registration successful!");
        try {
            handleGoToLogin(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
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
