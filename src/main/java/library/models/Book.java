package library.models;

import java.util.Date;

public class Book {
    private String title;
    private String author;
    private String abstractContent;
    private String status; // e.g., "pending", "approved", "rejected"
    private Date publishedDate;
    private int borrowCount;
    private String content; // The actual content of the book

    public Book(String title, String author, String abstractContent, String content) {
        this.title = title;
        this.author = author;
        this.abstractContent = abstractContent;
        this.content = content;
        this.status = "pending";
        this.publishedDate = null;
        this.borrowCount = 0;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
