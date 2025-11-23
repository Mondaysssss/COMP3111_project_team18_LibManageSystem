package library.models;

import java.util.Date;

/**
 * Represents a book in the library management system.
 * 
 * <p>This class encapsulates all information about a book including its metadata
 * (title, author, abstract), content, publication status, and usage statistics.
 * Books can be in one of three states: "pending" (awaiting librarian approval),
 * "approved" (available for borrowing), or "rejected" (not approved for publication).
 * 
 * <p>The class is designed to be serialized to/from JSON using Gson, which requires
 * a default no-argument constructor.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class Book {
    private String title;
    private String author;
    private String authorUsername; // Username of the author who published the book
    private String abstractContent;
    private String status; // e.g., "pending", "approved", "rejected"
    private Date publishedDate;
    private int borrowCount;
    private String content; // The actual content of the book
    private int readers; // Number of readers who have read the book

    /**
     * Default constructor for Gson deserialization.
     * Initializes a book with default values: status "pending", no published date,
     * and zero borrow count and readers.
     */
    public Book() {
        this.status = "pending";
        this.publishedDate = null;
        this.borrowCount = 0;
        this.readers = 0;
    }

    /**
     * Constructs a new Book with the specified details.
     * 
     * @param title the title of the book
     * @param author the author's name
     * @param abstractContent a brief summary or abstract of the book
     * @param content the full text content of the book
     */
    public Book(String title, String author, String abstractContent, String content) {
        this.title = title;
        this.author = author;
        this.abstractContent = abstractContent;
        this.content = content;
        this.status = "pending";
        this.publishedDate = null;
        this.borrowCount = 0;
        this.readers = 0;
    }

    /**
     * Gets the title of the book.
     * 
     * @return the book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     * 
     * @param title the book title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author's name.
     * 
     * @return the author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author's name.
     * 
     * @param author the author name to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the abstract or summary of the book.
     * 
     * @return the abstract content
     */
    public String getAbstractContent() {
        return abstractContent;
    }

    /**
     * Sets the abstract or summary of the book.
     * 
     * @param abstractContent the abstract content to set
     */
    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    /**
     * Gets the publication status of the book.
     * 
     * @return the status, which can be "pending", "approved", or "rejected"
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the publication status of the book.
     * 
     * @param status the status to set ("pending", "approved", or "rejected")
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the date when the book was published/approved.
     * 
     * @return the published date, or null if not yet published
     */
    public Date getPublishedDate() {
        return publishedDate;
    }

    /**
     * Sets the date when the book was published/approved.
     * 
     * @param publishedDate the published date to set
     */
    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * Gets the number of times this book has been borrowed.
     * 
     * @return the borrow count
     */
    public int getBorrowCount() {
        return borrowCount;
    }

    /**
     * Sets the number of times this book has been borrowed.
     * 
     * @param borrowCount the borrow count to set
     */
    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }
    
    /**
     * Gets the full text content of the book.
     * 
     * @return the book content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the full text content of the book.
     * 
     * @param content the book content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the username of the author who published this book.
     * 
     * @return the author's username
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * Sets the username of the author who published this book.
     * 
     * @param authorUsername the author's username to set
     */
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    /**
     * Gets the number of readers who have read this book.
     * 
     * @return the number of readers
     */
    public int getReaders() {
        return readers;
    }

    /**
     * Sets the number of readers who have read this book.
     * 
     * @param readers the number of readers to set
     */
    public void setReaders(int readers) {
        this.readers = readers;
    }
}
