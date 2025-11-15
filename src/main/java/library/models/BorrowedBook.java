package library.models;

import java.util.Date;

public class BorrowedBook {
    private String bookTitle;
    private String borrowerUsername;
    private Date borrowDate;
    private Date returnDate;

    public BorrowedBook(String bookTitle, String borrowerUsername, Date borrowDate, Date returnDate) {
        this.bookTitle = bookTitle;
        this.borrowerUsername = borrowerUsername;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    // Getters and Setters
    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBorrowerUsername() {
        return borrowerUsername;
    }

    public void setBorrowerUsername(String borrowerUsername) {
        this.borrowerUsername = borrowerUsername;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
