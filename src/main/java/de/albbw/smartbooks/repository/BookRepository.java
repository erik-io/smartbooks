package de.albbw.smartbooks.repository;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Das BookRepository ist ein Repository-Interface, das die Datenzugriffslogik
 * für die Entität Book bereitstellt. Es erweitert JpaRepository und bietet Methoden
 * für CRUD-Operationen sowie benutzerdefinierte Abfragen.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    /**
     * Sucht ein Buch anhand seiner ISBN.
     *
     * @param isbn die ISBN des Buches, das gesucht werden soll
     * @return ein Optional, das das gefundene Buch enthält, oder leer, wenn kein Buch gefunden wurde
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Überprüft, ob ein Buch mit der angegebenen ISBN existiert.
     *
     * @param isbn die ISBN des Buches, das überprüft werden soll
     * @return true, wenn ein Buch mit der angegebenen ISBN existiert, andernfalls false
     */
    boolean existsByIsbn(String isbn);

    /**
     * Löscht ein Buch aus der Datenbank basierend auf seiner ISBN.
     *
     * @param isbn die ISBN des Buches, das gelöscht werden soll
     */
    void deleteByIsbn(String isbn);

    /**
     * Sucht alle Bücher, die einem bestimmten Genre zugeordnet sind.
     *
     * @param genre das Genre, nach dem die Bücher gefiltert werden sollen
     * @return eine Liste von Büchern, die dem angegebenen Genre entsprechen;
     * gibt eine leere Liste zurück, wenn keine Bücher gefunden werden
     */
    List<Book> findByGenre(String genre);

    /**
     * Sucht Bücher basierend auf dem angegebenen Lesestatus.
     *
     * @param status der Lesestatus des Buches, z. B. READ, PLANNED, READING oder UNKNOWN
     * @return eine Liste von Büchern, die den angegebenen Lesestatus haben
     */
    List<Book> findByStatus(ReadingStatus status);


    /**
     * Sucht alle Bücher, die von einem bestimmten Autor verfasst wurden.
     *
     * @param author der Name des Autors, nach dem die Bücher gefiltert werden sollen
     * @return eine Liste von Büchern, die von dem angegebenen Autor verfasst wurden;
     * gibt eine leere Liste zurück, wenn keine Bücher gefunden werden
     */
    List<Book> findByAuthor(String author);
}
