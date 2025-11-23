package library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Main entry point for the Library Management System JavaFX application.
 * 
 * <p>This class extends JavaFX Application and initializes the primary stage
 * with the Home screen, which serves as the role selection interface. It also
 * provides static access to the primary stage for use by controllers that need
 * to open new windows or dialogs.
 * 
 * <p>The application supports multiple user roles: students/staff, authors, and
 * librarians, each with their own dashboard and functionality.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class Main extends Application {

    /** The primary JavaFX Stage, made available statically. */
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        // Show the Home (role‐select) first, not the Login screen directly:
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/Home.fxml")));
        stage.setTitle("Library Management System");
        stage.setScene(new Scene(root, 640, 480));
        stage.show();
    }

    /** @return the primary Stage for file-choosers, new windows, etc. */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
