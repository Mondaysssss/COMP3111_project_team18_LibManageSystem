package library.utils;

import library.models.User;

/**
 * Utility class for managing the currently logged-in user session.
 * 
 * <p>This class provides a thread-local-like mechanism to store and retrieve
 * the currently authenticated user. It uses a static field to maintain the
 * user session throughout the application lifecycle.
 * 
 * <p>The current user is typically set after successful login and cleared
 * when the user logs out.
 * 
 * @author Library Management System Team
 * @version 1.0
 */
public class CurrentUser {
    private static User currentUser;

    /**
     * Gets the currently logged-in user.
     * 
     * @return the current user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the currently logged-in user.
     * 
     * @param currentUser the user to set as the current user, or null to clear
     *                    the current session
     */
    public static void setCurrentUser(User currentUser) {
        CurrentUser.currentUser = currentUser;
    }
}
