package library.models;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookTest {

    @Test
    void defaultConstructorShouldInitializeDefaults() {
        Book book = new Book();

        assertEquals("pending", book.getStatus());
        assertNull(book.getPublishedDate());
        assertEquals(0, book.getBorrowCount());
        assertEquals(0, book.getReaders());
    }

    @Test
    void parameterizedConstructorShouldSetFields() {
        Book book = new Book("Title", "Author", "Abstract", "Content");

        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertEquals("Abstract", book.getAbstractContent());
        assertEquals("Content", book.getContent());
        assertEquals("pending", book.getStatus());
    }

    @Test
    void settersShouldUpdateValues() {
        Book book = new Book();
        Date published = new Date();

        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setAbstractContent("New Abstract");
        book.setContent("Body");
        book.setStatus("approved");
        book.setAuthorUsername("author_user");
        book.setBorrowCount(3);
        book.setReaders(10);
        book.setPublishedDate(published);

        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals("New Abstract", book.getAbstractContent());
        assertEquals("Body", book.getContent());
        assertEquals("approved", book.getStatus());
        assertEquals("author_user", book.getAuthorUsername());
        assertEquals(3, book.getBorrowCount());
        assertEquals(10, book.getReaders());
        assertEquals(published, book.getPublishedDate());
    }
}



