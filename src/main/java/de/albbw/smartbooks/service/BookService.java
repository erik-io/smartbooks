package de.albbw.smartbooks.service;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.ReadingStatus;
import de.albbw.smartbooks.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists.");
        }
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
}
