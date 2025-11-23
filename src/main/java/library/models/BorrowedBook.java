package library.models;

import java.util.Date;

/**
 * Represents a book borrowing record in the library management system.
 * 
 * <p>This class tracks when a book was borrowed, by whom, and when it should be
 * returned. It links a book title to a borrower's username and maintains
 * temporal information about the borrowing period.
 * 
 * <p>The class is designed to be serialized to/from JSON using Gson.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class BorrowedBook {
    private String bookTitle;
    private String borrowerUsername;
    private Date borrowDate;
    private Date returnDate;

    /**
     * Constructs a new BorrowedBook record with the specified details.
     * 
     * @param bookTitle the title of the borrowed book
     * @param borrowerUsername the username of the user who borrowed the book
     * @param borrowDate the date when the book was borrowed
     * @param returnDate the date when the book should be returned
     */
    public BorrowedBook(String bookTitle, String borrowerUsername, Date borrowDate, Date returnDate) {
        this.bookTitle = bookTitle;
        this.borrowerUsername = borrowerUsername;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    /**
     * Gets the title of the borrowed book.
     * 
     * @return the book title
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Sets the title of the borrowed book.
     * 
     * @param bookTitle the book title to set
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Gets the username of the borrower.
     * 
     * @return the borrower's username
     */
    public String getBorrowerUsername() {
        return borrowerUsername;
    }

    /**
     * Sets the username of the borrower.
     * 
     * @param borrowerUsername the borrower's username to set
     */
    public void setBorrowerUsername(String borrowerUsername) {
        this.borrowerUsername = borrowerUsername;
    }

    /**
     * Gets the date when the book was borrowed.
     * 
     * @return the borrow date
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the date when the book was borrowed.
     * 
     * @param borrowDate the borrow date to set
     */
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the date when the book should be returned.
     * 
     * @return the return date
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the date when the book should be returned.
     * 
     * @param returnDate the return date to set
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
