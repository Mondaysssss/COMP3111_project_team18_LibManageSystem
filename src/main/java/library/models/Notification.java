package library.models;

/**
 * Represents a notification message in the library management system.
 * 
 * <p>This class encapsulates notification messages sent to users, typically
 * regarding book approval/rejection, borrowing status, or other system events.
 * Notifications can be marked as read or unread to track whether the user has
 * seen them.
 * 
 * <p>The class is designed to be serialized to/from JSON using Gson, which requires
 * a default no-argument constructor.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class Notification {
    private String message;
    private String authorUsername;
    private boolean read;

    /**
     * Default constructor for Gson deserialization.
     * Initializes a notification with read status set to false.
     */
    public Notification() {
        this.read = false;
    }

    /**
     * Constructs a new Notification with the specified message and recipient.
     * 
     * @param message the notification message text
     * @param authorUsername the username of the user who should receive this notification
     */
    public Notification(String message, String authorUsername) {
        this.message = message;
        this.authorUsername = authorUsername;
        this.read = false;
    }

    /**
     * Gets the notification message text.
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the notification message text.
     * 
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the username of the notification recipient.
     * 
     * @return the author username
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * Sets the username of the notification recipient.
     * 
     * @param authorUsername the author username to set
     */
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    /**
     * Checks whether this notification has been read.
     * 
     * @return true if the notification has been read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status of this notification.
     * 
     * @param read true if the notification has been read, false otherwise
     */
    public void setRead(boolean read) {
        this.read = read;
    }
}

