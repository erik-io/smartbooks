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

/**
 * Dieser Controller stellt Endpunkte für die Verwaltung von Büchern bereit.
 * Er ermöglicht Operationen wie das Hinzufügen, Abrufen, Aktualisieren und Löschen von Büchern.
 * Die Endpunkte verwenden JSON als Datenformat und sind über den Pfad "/api/books" zugänglich.
 * <p>
 * Die Klasse verwendet den Service {@link BookService}, um die zugrunde liegenden
 * Geschäftslogiken für Buchoperationen auszuführen.
 */
@RestController
@RequestMapping(value = "/api/books", produces = "application/json")
// Wir brauchen eine explizite produces-Angabe, weil jackson-dataformat-xml im Klassenpfad ist und Spring Boot es dann als bevorzugtes Format für die Content Negotiation wählt
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Diese Methode fügt ein neues Buch hinzu, das über die Anfrage bereitgestellt wird.
     * Das Buch wird intern mit einer Datenquelle markiert und anschließend gespeichert.
     *
     * @param book Das Buchobjekt, das der Anfragekörper enthält und hinzugefügt werden soll.
     *             Es wird erwartet, dass das Buch eine gültige ISBN hat, und weitere
     *             Eigenschaften wie Titel, Autor, etc. können angegeben sein.
     * @return Eine ResponseEntity mit unterschiedlichen HTTP-Statuscodes:
     * - 201 (CREATED): Wenn das Buch erfolgreich gespeichert wurde.
     * - 409 (CONFLICT): Wenn die ISBN des Buches leer ist oder bereits existiert.
     * - 500 (INTERNAL_SERVER_ERROR): Wenn ein unerwarteter Fehler auftritt.
     */
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

    /**
     * Ruft eine Liste aller gespeicherten Bücher ab.
     *
     * @return eine ResponseEntity, die eine Liste aller Bücher enthält
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    /**
     * Sucht ein Buch basierend auf der angegebenen ISBN und gibt es zurück, falls verfügbar.
     *
     * @param isbn die ISBN des Buches, das abgerufen werden soll
     * @return eine ResponseEntity, die entweder das gefundene Buch (mit HTTP-Status 200 OK)
     * oder einen leeren Körper mit HTTP-Status 404 Not Found enthält, falls kein Buch gefunden wurde
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.findByIsbn(isbn);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Aktualisiert die Details eines Buches basierend auf der angegebenen ISBN.
     * Diese Methode überprüft, ob die ISBN im Pfad mit der ISBN im Anfragetext übereinstimmt,
     * bevor das Buch aktualisiert wird.
     *
     * @param isbn        Die ISBN des Buches, das aktualisiert werden soll. Sie wird aus dem Pfad extrahiert.
     * @param bookDetails Die neuen Details des Buches, die als Anfrageinhalt bereitgestellt werden.
     * @return Eine ResponseEntity mit der folgenden Antwort:
     * - HTTP 200 (OK), wenn das Buch erfolgreich aktualisiert wurde.
     * - HTTP 400 (Bad Request), wenn die ISBN im Pfad nicht mit der ISBN im Anfrageinhalt übereinstimmt.
     * - HTTP 404 (Not Found), wenn kein Buch mit der angegebenen ISBN gefunden wurde.
     * - HTTP 500 (Internal Server Error), wenn ein unerwarteter Fehler auftritt.
     */
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

    /**
     * Löscht ein Buch anhand der angegebenen ISBN aus dem Repository.
     * Sollte kein Buch mit der angegebenen ISBN existieren, wird ein Fehler zurückgegeben.
     *
     * @param isbn die ISBN des zu löschenden Buches
     * @return ResponseEntity mit folgendem Status:
     * - 204 NO CONTENT, wenn das Buch erfolgreich gelöscht wurde
     * - 404 NOT FOUND, wenn kein Buch mit der angegebenen ISBN gefunden wurde
     * - 500 INTERNAL SERVER ERROR, wenn ein unerwarteter Fehler auftritt
     */
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

    @PostMapping("/{isbn}/fetch-api-data")
    public ResponseEntity<?> fetchAndUpdateBook(@PathVariable String isbn) {
        try {
            Book updatedBook = bookService.fetchAndUpdateBookFromApi(isbn);
            return ResponseEntity.ok(updatedBook); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 NOT FOUND
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while fetching and updating the book. " + e.getMessage()); // 500 INTERNAL SERVER ERROR
        }
    }
}
