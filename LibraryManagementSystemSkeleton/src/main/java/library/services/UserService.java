package library.services;

import library.models.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for managing user authentication and registration.
 * Uses file-based storage to persist user data.
 */
public class UserService {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = "users.dat";
    private static UserService instance;
    private Map<String, User> users;
    
    private UserService() {
        users = new HashMap<>();
        loadUsers();
    }
    
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    /**
     * Registers a new user.
     * @param username The username
     * @param password The password
     * @param fullName The full name
     * @param role The user role (student, author, librarian)
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String password, String fullName, String role) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        
        String normalizedUsername = username.trim().toLowerCase();
        if (users.containsKey(normalizedUsername)) {
            return false; // Username already exists
        }
        
        User newUser = new User(normalizedUsername, password, fullName.trim(), role.toLowerCase());
        users.put(normalizedUsername, newUser);
        saveUsers();
        return true;
    }
    
    /**
     * Authenticates a user.
     * @param username The username
     * @param password The password
     * @param role The expected role
     * @return The User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password, String role) {
        if (username == null || password == null) {
            return null;
        }
        
        String normalizedUsername = username.trim().toLowerCase();
        User user = users.get(normalizedUsername);
        
        if (user == null) {
            return null; // User not found
        }
        
        // Check password
        if (!user.getPassword().equals(password)) {
            return null; // Invalid password
        }
        
        // Check role matches
        if (!user.getRole().equalsIgnoreCase(role)) {
            return null; // Role mismatch
        }
        
        return user;
    }
    
    /**
     * Checks if a username already exists.
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        if (username == null) {
            return false;
        }
        return users.containsKey(username.trim().toLowerCase());
    }
    
    /**
     * Loads users from the data file.
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        Path dataDir = Paths.get(DATA_DIR);
        Path usersFile = dataDir.resolve(USERS_FILE);
        
        if (!Files.exists(usersFile)) {
            // Create data directory if it doesn't exist
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                System.err.println("Failed to create data directory: " + e.getMessage());
            }
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usersFile.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                users = (Map<String, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load users: " + e.getMessage());
            users = new HashMap<>();
        }
    }
    
    /**
     * Saves users to the data file.
     */
    private void saveUsers() {
        Path dataDir = Paths.get(DATA_DIR);
        Path usersFile = dataDir.resolve(USERS_FILE);
        
        try {
            Files.createDirectories(dataDir);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(usersFile.toFile()))) {
                oos.writeObject(users);
            }
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }
}


