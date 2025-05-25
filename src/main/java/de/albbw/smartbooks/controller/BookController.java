package de.albbw.smartbooks.controller;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import de.albbw.smartbooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<?> addNewBook(@RequestBody Book book) {
        try {
            book.setSource(DataSource.API);
            Book savedBook = bookService.saveBook(book);
            return new ResponseEntity<>(savedBook, HttpStatus.CREATED); // 201 CREATED
        } catch (IllegalArgumentException e) { // 409 CONFLICT, wegen existierender oder leerer ISBN
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) { // 500 INTERNAL SERVER ERROR
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<?> updateBook(@PathVariable String isbn, @RequestBody Book bookDetails) {
        try {
            if (bookDetails.getIsbn() != null && !bookDetails.getIsbn().equals(isbn)) {
                return ResponseEntity.badRequest().body("ISBN in path does not match ISBN in body.");
            }
            bookService.updateBook(bookDetails.getIsbn(), bookDetails);
            return ResponseEntity.ok(bookDetails); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while updating the book.");
        }
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> deleteBook(@PathVariable String isbn) {
        try {
            bookService.deleteBookByIsbn(isbn);
            return ResponseEntity.noContent().build(); // 204 NO CONTENT
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while deleting the book.");
        }
    }
}
