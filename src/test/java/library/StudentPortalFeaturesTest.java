package library;

import library.models.Book;
import library.models.BorrowedBook;
import library.models.Notification;
import library.models.User;
import library.utils.FileUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration-style tests that mimic the main student/staff portal flows by
 * exercising the persistence layer end-to-end.
 */
class StudentPortalFeaturesTest extends AbstractDataFileTest {

    @Test
    void studentRegistrationShouldPersistNewAccount() {
        String username = "student_register";
        List<User> users = FileUtil.readUsers();
        users.removeIf(u -> username.equals(u.getUsername()));
        FileUtil.writeUsers(users);

        User newStudent = new User(username, "pass123", "Student Tester", "student");
        users.add(newStudent);
        FileUtil.writeUsers(users);

        boolean exists = FileUtil.readUsers().stream()
            .anyMatch(u -> username.equals(u.getUsername()) && "student".equals(u.getRole()));

        assertTrue(exists, "Student registration should result in a persisted account.");
    }

    @Test
    void borrowingBookShouldCreateBorrowRecordAndIncrementStats() {
        Book book = new Book("Integration Book", "Author", "Abstract", "Body");
        book.setAuthorUsername("author");
        book.setStatus("approved");
        FileUtil.writeBooks(new ArrayList<>(List.of(book)));
        FileUtil.writeBorrowedBooks(new ArrayList<>());

        String borrower = "student_borrow";
        List<User> users = FileUtil.readUsers();
        users.removeIf(u -> borrower.equals(u.getUsername()));
        users.add(new User(borrower, "pass", "Borrower", "student"));
        FileUtil.writeUsers(users);

        List<BorrowedBook> borrowedBooks = FileUtil.readBorrowedBooks();
        BorrowedBook record = new BorrowedBook(book.getTitle(), borrower, new Date(), new Date(System.currentTimeMillis() + 3600_000));
        borrowedBooks.add(record);
        FileUtil.writeBorrowedBooks(borrowedBooks);

        List<Book> reloadedBooks = FileUtil.readBooks();
        for (Book candidate : reloadedBooks) {
            if (candidate.getTitle().equals(book.getTitle())) {
                candidate.setBorrowCount(candidate.getBorrowCount() + 1);
            }
        }
        FileUtil.writeBooks(reloadedBooks);

        List<BorrowedBook> finalBorrowed = FileUtil.readBorrowedBooks();
        assertEquals(1, finalBorrowed.size());
        assertEquals(borrower, finalBorrowed.get(0).getBorrowerUsername());

        int updatedCount = FileUtil.readBooks().stream()
            .filter(b -> book.getTitle().equals(b.getTitle()))
            .findFirst()
            .map(Book::getBorrowCount)
            .orElseThrow();
        assertEquals(1, updatedCount, "Borrow action should increment borrow count.");
    }

    @Test
    void returningBookShouldRemoveBorrowRecordAndNotify() {
        String borrower = "student_return";
        BorrowedBook borrowedBook = new BorrowedBook("Return Book", borrower, new Date(), new Date(System.currentTimeMillis() + 1000));
        FileUtil.writeBorrowedBooks(new ArrayList<>(List.of(borrowedBook)));
        FileUtil.writeNotifications(new ArrayList<>());

        List<BorrowedBook> allBorrowed = FileUtil.readBorrowedBooks();
        allBorrowed.removeIf(bb -> borrower.equals(bb.getBorrowerUsername()) && "Return Book".equals(bb.getBookTitle()));
        FileUtil.writeBorrowedBooks(allBorrowed);

        List<Notification> notifications = FileUtil.readNotifications();
        notifications.add(new Notification("The book \"Return Book\" has been returned.", borrower));
        FileUtil.writeNotifications(notifications);

        assertTrue(FileUtil.readBorrowedBooks().stream()
            .noneMatch(bb -> borrower.equals(bb.getBorrowerUsername())), "Returned book should no longer exist.");

        long messageCount = FileUtil.readNotifications().stream()
            .filter(n -> borrower.equals(n.getAuthorUsername()))
            .count();
        assertEquals(1, messageCount, "Returning a book should push a notification to the student.");
    }
}



