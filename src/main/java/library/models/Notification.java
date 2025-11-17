package library.models;

public class Notification {
    private String message;
    private String authorUsername;
    private boolean read;

    // Default constructor for Gson
    public Notification() {
        this.read = false;
    }

    public Notification(String message, String authorUsername) {
        this.message = message;
        this.authorUsername = authorUsername;
        this.read = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}

