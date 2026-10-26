package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.entity.Book;
import bootcamp.hibernate_practical.exception.BookNotFoundException;
import bootcamp.hibernate_practical.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(BookService.class)
class BookServiceTest {
    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void createsBookAndMapsSavedEntityToResponse() {
        BookResponse response = bookService.createBook(
                createRequest("Pride and Prejudice", "Jane Austen", "Classic", 1813)
        );

        assertNotNull(response.id);
        assertEquals("Pride and Prejudice", response.title);
        assertEquals("Jane Austen", response.author);
        assertEquals("Classic", response.genre);
        assertEquals(1813, response.publicationYear);
        assertTrue(response.available);
    }

    @Test
    void retrievesBooksAndMapsEveryEntityField() {
        Book book = bookRepository.save(
                new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy", 1937, false)
        );

        BookResponse byId = bookService.getBookById(book.getId());
        var allBooks = bookService.getAllBooks();

        assertEquals(book.getId(), byId.id);
        assertEquals(book.getTitle(), byId.title);
        assertEquals(book.getAuthor(), byId.author);
        assertEquals(book.getGenre(), byId.genre);
        assertEquals(book.getPublicationYear(), byId.publicationYear);
        assertEquals(book.isAvailable(), byId.available);
        assertEquals(Set.of(book.getId()), allBooks.stream()
                .map(response -> response.id)
                .collect(Collectors.toSet()));
    }

    @Test
    void updatesAndDeletesBook() {
        Book book = bookRepository.save(
                new Book("Old Title", "Old Author", "Old Genre", 1990, true)
        );
        UpdateBookRequest request = updateRequest("New Title", "New Author", "New Genre", 2024, false);

        BookResponse updated = bookService.updateBook(book.getId(), request);

        assertEquals("New Title", updated.title);
        assertEquals("New Author", updated.author);
        assertEquals("New Genre", updated.genre);
        assertEquals(2024, updated.publicationYear);
        assertFalse(updated.available);

        bookService.deleteBook(book.getId());

        assertFalse(bookRepository.existsById(book.getId()));
    }

    @Test
    void searchesByAuthorAvailabilityAndPublicationYearAndCountsBooks() {
        Book matchingBook = bookRepository.save(
                new Book("Available", "Jane Austen", "Classic", 1813, true)
        );
        Book unavailableBook = bookRepository.save(
                new Book("Unavailable", "Jane Austen", "Classic", 1814, false)
        );
        Book otherAuthorBook = bookRepository.save(
                new Book("Newer", "Other Author", "Fiction", 2020, true)
        );

        assertEquals(
                Set.of(matchingBook.getId(), otherAuthorBook.getId()),
                bookService.findAvailableBooks().stream()
                        .map(response -> response.id)
                        .collect(Collectors.toSet())
        );
        assertEquals(
                Set.of(matchingBook.getId(), unavailableBook.getId()),
                bookService.findByAuthor("Jane Austen").stream()
                        .map(response -> response.id)
                        .collect(Collectors.toSet())
        );
        assertEquals(
                Set.of(unavailableBook.getId(), otherAuthorBook.getId()),
                bookService.findBooksPublishedAfter(1813).stream()
                        .map(response -> response.id)
                        .collect(Collectors.toSet())
        );
        assertEquals(3, bookService.countBooks());
    }

    @Test
    void throwsBookNotFoundExceptionForMissingIds() {
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(999L));
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(999L));
        assertThrows(
                BookNotFoundException.class,
                () -> bookService.updateBook(999L, updateRequest("Title", "Author", "Genre", 2024, true))
        );
    }

    private static CreateBookRequest createRequest(String title, String author, String genre, int publicationYear) {
        CreateBookRequest request = new CreateBookRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "author", author);
        ReflectionTestUtils.setField(request, "genre", genre);
        ReflectionTestUtils.setField(request, "publicationYear", publicationYear);
        return request;
    }

    private static UpdateBookRequest updateRequest(
            String title, String author, String genre, int publicationYear, boolean available
    ) {
        UpdateBookRequest request = new UpdateBookRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "author", author);
        ReflectionTestUtils.setField(request, "genre", genre);
        ReflectionTestUtils.setField(request, "publicationYear", publicationYear);
        ReflectionTestUtils.setField(request, "available", available);
        return request;
    }
}
