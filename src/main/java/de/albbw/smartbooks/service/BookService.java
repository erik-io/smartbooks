package de.albbw.smartbooks.service;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import de.albbw.smartbooks.model.ReadingStatus;
import de.albbw.smartbooks.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Diese Service-Klasse stellt die Geschäftslogik für die Verwaltung von Büchern bereit.
 * <p>
 * Sie dient als Vermittler zwischen der Controller-Schicht und der Repository-Schicht,
 * um eine klare Trennung der Verantwortlichkeiten zu gewährleisten.
 * <p>
 * Die Klasse nutzt ein {@link BookRepository}, um auf die zugrunde liegenden
 * Datenbankoperationen zuzugreifen, wie etwa das Abrufen, Speichern oder Löschen von Büchern.
 * <p>
 * Abhängigkeiten werden bei der Erstellung dieser Klasse durch Dependency Injection
 * bereitgestellt, um die Testbarkeit und Modularität zu verbessern.
 */
@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final OpenLibraryService openLibraryService;

    public BookService(BookRepository bookRepository, OpenLibraryService openLibraryService) {
        this.bookRepository = bookRepository;
        this.openLibraryService = openLibraryService;
    }

    /**
     * Speichert ein Buch im Repository, sofern ein Buch mit derselben ISBN nicht bereits vorhanden ist.
     *
     * @param book das Buch, das gespeichert werden soll
     * @return das gespeicherte Buch
     * @throws IllegalArgumentException wenn ein Buch mit der ISBN des übergebenen Buchs bereits existiert
     */
    @Transactional
    public Book saveBook(Book book) {
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN of book cannot be null or empty.");
        }

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists.");
        }

        if (book.getSource() == null) {
            book.setSource(DataSource.UNKNOWN);
        }

        if (book.getStatus() == null) {
            book.setStatus(ReadingStatus.UNKNOWN);
        }

        book.setId(null);
        return bookRepository.save(book);
    }

    /**
     * Sucht ein Buch anhand seiner ISBN.
     *
     * @param isbn die ISBN des Buches, das gesucht werden soll
     * @return ein Optional, das das gefundene Buch enthält, oder leer, wenn kein Buch gefunden wurde
     */
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Ruft alle Bücher aus dem Repository ab.
     *
     * @return eine Liste aller im Repository gespeicherten Bücher
     */
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Löscht ein Buch aus dem Repository anhand seiner ISBN.
     * <p>
     * Wenn kein Buch mit der angegebenen ISBN gefunden wird, wird eine IllegalArgumentException ausgelöst.
     *
     * @param isbn die ISBN des zu löschenden Buches
     * @throws IllegalArgumentException wenn kein Buch mit der angegebenen ISBN existiert
     */
    @Transactional
    public void deleteBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN of book cannot be null or empty.");
        }
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isPresent()) {
            bookRepository.deleteByIsbn(isbn);
        } else {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " does not exist.");
        }
    }

    /**
     * Aktualisiert die Details eines bestehenden Buches anhand seiner ISBN.
     * Wenn kein Buch mit der angegebenen ISBN existiert, wird eine IllegalArgumentException ausgelöst.
     *
     * @param isbn        die ISBN des Buches, das aktualisiert werden soll
     * @param newBookInfo die neuen Details des Buches, die gespeichert werden sollen
     * @return ein Optional, das das aktualisierte Buch enthält, wenn die Aktualisierung erfolgreich war
     * @throws IllegalArgumentException wenn kein Buch mit der angegebenen ISBN existiert
     */
    @Transactional
    public Optional<Book> updateBook(String isbn, Book newBookInfo) {
        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn); // Wir schauen, ob das Buch in der Datenbank vorhanden ist

        // Wenn kein Buch mit der angegebenen ISBN existiert, werfen wir eine IllegalArgumentException
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book with ISBN " + isbn + " does not exist.");
        }

        // Das vorher in dem Optional gespeicherte Buch wird mit der Methode .get() abgerufen.
        // Die Felder des gefundenen Buches (bookToUpdate) werden mit den Werten aus dem Objekt newBookInfo überschrieben. Dazu werden Setter-Methoden verwendet, die von Lombok automatisch in der Book-Klasse generiert wurden.
        Book bookToUpdate = bookOptional.get();
        bookToUpdate.setTitle(newBookInfo.getTitle());
        bookToUpdate.setAuthor(newBookInfo.getAuthor());
        bookToUpdate.setGenre(newBookInfo.getGenre());
        bookToUpdate.setPublicationYear(newBookInfo.getPublicationYear());
        bookToUpdate.setPublisher(newBookInfo.getPublisher());
        bookToUpdate.setPageCount(newBookInfo.getPageCount());
        bookToUpdate.setCoverImageUrl(newBookInfo.getCoverImageUrl());
        bookToUpdate.setStatus(newBookInfo.getStatus());
        bookToUpdate.setSource(newBookInfo.getSource());

        // Das gespeicherte Buch wird in ein Optional verpackt und zurückgegeben.
        return Optional.of(bookRepository.save(bookToUpdate));
    }

    /**
     * Findet alle Bücher, die den angegebenen Lesestatus haben.
     *
     * @param status der Lesestatus, nach dem die Bücher gefiltert werden sollen
     * @return eine Liste von Büchern mit dem angegebenen Lesestatus
     */
    public List<Book> findBooksByReadingStatus(ReadingStatus status) {
        return bookRepository.findByStatus(status);
    }

    /**
     * Findet eine Liste von Büchern basierend auf ihrem Genre.
     *
     * @param genre das Genre der Bücher, die gefunden werden sollen
     * @return eine Liste von Büchern, die dem angegebenen Genre entsprechen
     */
    public List<Book> findBooksByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }

    /**
     * Findet alle Bücher, die von einem bestimmten Autor geschrieben wurden.
     *
     * @param author der Autor, nach dem die Bücher gefiltert werden sollen
     * @return eine Liste von Büchern, die von dem angegebenen Autor geschrieben wurden
     */
    public List<Book> findBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    /**
     * Verarbeitet und speichert eine Liste importierter Bücher in der Datenbank, sofern sie eine gültige ISBN besitzen.
     * Bereits existierende Bücher (basierend auf ISBN) werden übersprungen.
     *
     * @param listOfBooks die Liste der zu importierenden Bücher; Null- oder leere Listen werden ignoriert
     * @param dataSource  die Quelle, aus der die Bücher importiert wurden; wird für Protokollierungszwecke verwendet
     */
    @Transactional
    public void processAndSaveImportedBooks(List<Book> listOfBooks, DataSource dataSource) {
        if (listOfBooks == null || listOfBooks.isEmpty()) {
            log.info("'{}' contains no books to import.", dataSource);
            return;
        }

        for (Book book : listOfBooks) {
            if (book.getIsbn() == null) {
                log.warn("[{}] '{}' has no ISBN. Skipping import of book.", dataSource, book.getTitle());
                continue;
            }

            findByIsbn(book.getIsbn()).ifPresentOrElse(
                    existingBook -> {
                        log.info("[{}] '{}' (ISBN: {}) already exists in database. Skipping import.", dataSource, book.getTitle(), book.getIsbn());
                    },
                    () -> {
                        book.setId(null);
                        book.setSource(dataSource);
                        try {
                            bookRepository.save(book);
                            log.info("[{}] Importing '{}' (ISBN: {}) into database.", dataSource, book.getTitle(), book.getIsbn());
                        } catch (DataIntegrityViolationException e) {
                            log.error("[{}] Error while saving '{}' (ISBN: {}) into database: {}", dataSource, book.getTitle(), book.getIsbn(), e.getMessage());
                        } catch (Exception e) {
                            log.error("[{}] An unexpected error occurred during import of '{}' (ISBN: {}): {}", dataSource, book.getTitle(), book.getIsbn(), e.getMessage());
                        }
                    }
            );
        }
    }

    /**
     * Ruft Buchdetails von einer externen API (OpenLibrary) ab, aktualisiert diese
     * für das lokale Buch im Repository und speichert die Änderungen.
     *
     * @param isbn Die ISBN des Buches, das abgerufen und aktualisiert werden soll.
     * @return Das aktualisierte Buchobjekt aus dem Repository.
     * @throws IllegalArgumentException Wenn das Buch mit der angegebenen ISBN nicht
     *                                  in der lokalen Datenbank oder nicht in der OpenLibrary gefunden wird.
     */
    @Transactional
    public Book fetchAndUpdateBookFromApi(String isbn) {
        // Zuerst suchen wir das Buch in der lokalen Datenbank oder lösen eine Ausnahme aus
        Book localBook = bookRepository.findByIsbn(isbn).orElseThrow(() -> new IllegalArgumentException("Book with ISBN " + isbn + " not in database."));

        // Wir setzen einen Zeitstempel für die API-Überprüfung sofort
        localBook.setApiCheckTimestamp(LocalDateTime.now());

        Optional<Book> apiBookDataOptional = openLibraryService.fetchBookDetails(isbn);

        if (apiBookDataOptional.isEmpty()) {
            log.warn("Book with ISBN {} not found on OpenLibrary. Only updating check timestamp", isbn);
            return bookRepository.save(localBook);
        }

        Book apiBookData = apiBookDataOptional.get();
        boolean dataChanged = false;

        // Wir vergleichen die Felder und aktualisieren nur bei Änderungen

        // Wir aktualisieren das Buch mit den neuen Daten
        if (apiBookData.getTitle() != null && !Objects.equals(localBook.getTitle(), apiBookData.getTitle())) {
            localBook.setTitle(apiBookData.getTitle());
            dataChanged = true;
        }

        if (apiBookData.getAuthor() != null && !Objects.equals(localBook.getAuthor(), apiBookData.getAuthor())) {
            localBook.setAuthor(apiBookData.getAuthor());
            dataChanged = true;
        }

        if (apiBookData.getTitle() != null && !Objects.equals(localBook.getTitle(), apiBookData.getTitle())) {
            localBook.setTitle(apiBookData.getTitle());
            dataChanged = true;
        }

        if (apiBookData.getPublicationYear() != null && !Objects.equals(localBook.getPublicationYear(), apiBookData.getPublicationYear())) {
            localBook.setPublicationYear(apiBookData.getPublicationYear());
            dataChanged = true;
        }

        if (apiBookData.getPublisher() != null && !Objects.equals(localBook.getPublisher(), apiBookData.getPublisher())) {
            localBook.setPublisher(apiBookData.getPublisher());
            dataChanged = true;
        }

        // Bei Seitenzahl prüfen wir > 0, da die API manchmal 0 als Standardwert liefert.
        if (apiBookData.getPageCount() != null && apiBookData.getPageCount() > 0 && !Objects.equals(localBook.getPageCount(), apiBookData.getPageCount())) {
            localBook.setPageCount(apiBookData.getPageCount());
            dataChanged = true;
        }

        if (apiBookData.getCoverImageUrl() != null && !Objects.equals(localBook.getCoverImageUrl(), apiBookData.getCoverImageUrl())) {
            localBook.setCoverImageUrl(apiBookData.getCoverImageUrl());
        }

        if (dataChanged) {
            localBook.setApiDataUpdateTimestamp(LocalDateTime.now());
            log.info("{} (ISBN: {}) successfully updated with data from OpenLibrary.", localBook.getTitle(), localBook.getIsbn());
        } else {
            log.info("Data for {} (ISBN: {}) is already up-to-date. No changes made.", localBook.getTitle(), localBook.getIsbn());
        }

        return bookRepository.save(localBook);
    }
}