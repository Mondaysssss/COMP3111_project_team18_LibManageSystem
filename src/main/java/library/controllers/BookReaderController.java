package library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.models.Book;

public class BookReaderController {
    @FXML private ScrollPane scrollPane;
    @FXML private TextFlow textFlow;
    @FXML private Slider zoomSlider;
    @FXML private Label zoomLabel;
    
    private Book book;
    private double currentFontSize = 14.0;
    
    public void setBook(Book book) {
        this.book = book;
        displayBook();
    }
    
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

