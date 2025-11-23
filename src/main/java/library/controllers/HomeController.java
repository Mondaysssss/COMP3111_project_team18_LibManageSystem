package library.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.Main;

/**
 * Controller for the Home screen (role selection interface).
 * 
 * <p>This controller handles user interaction on the initial home screen where
 * users select their role (Student/Staff, Author, or Librarian) before proceeding
 * to the login screen. Each role selection navigates to the appropriate login
 * interface with the role context set.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class HomeController {

    /**
     * Handles the Student/Staff role selection button click.
     * Navigates to the login screen with the "student" role context.
     * 
     * @param event the action event triggered by the button click
     */
    @FXML
    private void handleStudent(ActionEvent event) {
        navigateToLogin("student");
    }

    /**
     * Handles the Author role selection button click.
     * Navigates to the login screen with the "author" role context.
     * 
     * @param event the action event triggered by the button click
     */
    @FXML
    private void handleAuthor(ActionEvent event) {
        navigateToLogin("author");
    }

    /**
     * Handles the Librarian role selection button click.
     * Navigates to the login screen with the "librarian" role context.
     * 
     * @param event the action event triggered by the button click
     */
    @FXML
    private void handleLibrarian(ActionEvent event) {
        navigateToLogin("librarian");
    }

    /**
     * Navigates to the login screen and sets the role context for the LoginController.
     * 
     * @param role the selected role ("student", "author", or "librarian")
     */
    private void navigateToLogin(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();

            // Pass the role to LoginController
            LoginController ctrl = loader.getController();
            ctrl.setRole(role);

            Stage stage = Main.getPrimaryStage();
            stage.setScene(new Scene(root, 640, 480));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
