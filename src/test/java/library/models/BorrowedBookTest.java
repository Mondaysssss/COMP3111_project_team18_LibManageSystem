package library.models;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BorrowedBookTest {

    @Test
    void constructorShouldPopulateAllFields() {
        Date borrowDate = new Date();
        Date returnDate = new Date(borrowDate.getTime() + 1000);

        BorrowedBook borrowedBook = new BorrowedBook("Book Title", "student", borrowDate, returnDate);

        assertEquals("Book Title", borrowedBook.getBookTitle());
        assertEquals("student", borrowedBook.getBorrowerUsername());
        assertEquals(borrowDate, borrowedBook.getBorrowDate());
        assertEquals(returnDate, borrowedBook.getReturnDate());
    }

    @Test
    void settersShouldUpdateValues() {
        BorrowedBook borrowedBook = new BorrowedBook("Old", "oldUser", new Date(), new Date());
        Date newBorrow = new Date(System.currentTimeMillis() - 1000);
        Date newReturn = new Date(System.currentTimeMillis() + 2000);

        borrowedBook.setBookTitle("New Title");
        borrowedBook.setBorrowerUsername("newUser");
        borrowedBook.setBorrowDate(newBorrow);
        borrowedBook.setReturnDate(newReturn);

        assertEquals("New Title", borrowedBook.getBookTitle());
        assertEquals("newUser", borrowedBook.getBorrowerUsername());
        assertEquals(newBorrow, borrowedBook.getBorrowDate());
        assertEquals(newReturn, borrowedBook.getReturnDate());
    }
}



