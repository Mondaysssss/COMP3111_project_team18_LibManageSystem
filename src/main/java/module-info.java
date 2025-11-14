module library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens library;
    exports library;

    opens library.controllers;
    exports library.controllers;
    
    exports library.models;
    exports library.services;
}