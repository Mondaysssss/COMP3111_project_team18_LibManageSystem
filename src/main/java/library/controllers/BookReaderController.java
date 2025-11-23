package library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.models.Book;

/**
 * Controller for the Book Reader interface.
 * 
 * <p>This controller manages the book reading interface where users can view
 * the full content of a borrowed book. It provides functionality to display
 * book content in a scrollable text area with adjustable font size via a zoom slider.
 * 
 * <p>The controller is typically opened from the Student Dashboard when a user
 * selects a borrowed book to read.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class BookReaderController {
    @FXML private ScrollPane scrollPane;
    @FXML private TextFlow textFlow;
    @FXML private Slider zoomSlider;
    @FXML private Label zoomLabel;
    
    private Book book;
    private double currentFontSize = 14.0;
    
    /**
     * Sets the book to be displayed in the reader and updates the display.
     * 
     * @param book the book to display
     */
    public void setBook(Book book) {
        this.book = book;
        displayBook();
    }
    
    /**
     * Displays the book content in the text flow with the current font size.
     * Also initializes the zoom slider to allow font size adjustment.
     */
    private void displayBook() {
        if (book == null) {
            return;
        }
        
        Text contentText = new Text(book.getContent());
        contentText.setFont(Font.font(currentFontSize));
        textFlow.getChildren().clear();
        textFlow.getChildren().add(contentText);
        
        zoomSlider.setMin(10);
        zoomSlider.setMax(36);
        zoomSlider.setValue(currentFontSize);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentFontSize = newVal.doubleValue();
            contentText.setFont(Font.font(currentFontSize));
        });
    }
}

