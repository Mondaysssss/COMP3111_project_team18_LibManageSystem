package library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small helper that lets tests safely mutate the JSON data files that back the
 * application by snapshotting the original content and restoring it afterwards.
 */
public final class TestDataHelper {

    private TestDataHelper() {
    }

    public static String backup(Path path) throws IOException {
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }
        return null;
    }

    public static void restore(Path path, String snapshot) throws IOException {
        if (snapshot == null) {
            Files.deleteIfExists(path);
        } else {
            Files.createDirectories(path.getParent());
            Files.writeString(path, snapshot, StandardCharsets.UTF_8);
        }
    }
}



