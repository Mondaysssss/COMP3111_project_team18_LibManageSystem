package library.models;

/**
 * Represents a user in the library management system.
 * 
 * <p>This class encapsulates user account information including credentials,
 * personal details, role, and account status. Users can have one of the following
 * roles: "student", "staff", "author", or "librarian". The status field indicates
 * whether the account is "active" or "inactive".
 * 
 * <p>The class is designed to be serialized to/from JSON using Gson.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class User {
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String status;

    /**
     * Constructs a new User with the specified details.
     * The user status is set to "active" by default.
     * 
     * @param username the unique username for login
     * @param password the user's password
     * @param fullName the user's full name
     * @param role the user's role ("student", "staff", "author", or "librarian")
     */
    public User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.status = "active"; // Default status
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's full name.
     * 
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the user's full name.
     * 
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the user's role.
     * 
     * @return the role ("student", "staff", "author", or "librarian")
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     * 
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the account status.
     * 
     * @return the status ("active" or "inactive")
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the account status.
     * 
     * @param status the status to set ("active" or "inactive")
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
