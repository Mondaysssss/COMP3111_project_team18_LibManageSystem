module library {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;
    requires org.apache.commons.io;

    opens library to javafx.fxml;
    opens library.controllers to javafx.fxml;
    opens library.models to com.google.gson, javafx.base; // Open models to Gson and JavaFX
    exports library;
    exports library.controllers;
    exports library.models;
}