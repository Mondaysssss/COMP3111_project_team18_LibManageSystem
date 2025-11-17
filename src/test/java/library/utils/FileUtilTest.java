package library.utils;

import library.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {
    private static final String USERS_FILE_PATH = "data/users.json";
    private File usersFile = new File(USERS_FILE_PATH);

    @BeforeEach
    void setUp() throws IOException {
        if (usersFile.exists()) {
            usersFile.delete();
        }
        usersFile.createNewFile();
    }

    @AfterEach
    void tearDown() {
        if (usersFile.exists()) {
            usersFile.delete();
        }
    }
    @Test
    void testWriteAndReadUsers() {
        List<User> originalUsers = new ArrayList<>();
        originalUsers.add(new User("testuser1", "pass1", "Test User One", "student"));
        originalUsers.add(new User("testuser2", "pass2", "Test User Two", "librarian"));

        FileUtil.writeUsers(originalUsers);

        List<User> readUsers = FileUtil.readUsers();

        assertNotNull(readUsers);
        assertEquals(2, readUsers.size());
        assertEquals("testuser1", readUsers.get(0).getUsername());
    }

    @Test
    void testReadFromEmptyFile() {

        List<User> users = FileUtil.readUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}