package de.albbw.smartbooks.repository;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Schnittstelle für den Zugriff auf und die Verwaltung von {@link Book}-Entitäten in der Datenbank.
 *
 * Diese Schnittstelle erweitert {@link org.springframework.data.jpa.repository.JpaRepository JpaRepository},
 * wodurch sie automatisch eine umfassende Menge an Standardmethoden für CRUD-Operationen (Create, Read, Update, Delete)
 * sowie Paginierungs- und Sortierfunktionalitäten erbt. Es ist also nicht notwendig, Methoden wie
 * {@code save()}, {@code findById()}, {@code findAll()}, {@code deleteById()} etc. explizit zu deklarieren.
 *
 * <p>Einige der wichtigsten automatisch bereitgestellten Methoden umfassen:
 * <ul>
 * <li>{@code <S extends Book> S save(S entity)} zum Speichern oder Aktualisieren eines Buches.</li>
 * <li>{@code Optional<Book> findById(Long id)} zum Abrufen eines Buches anhand seiner ID.</li>
 * <li>{@code List<Book> findAll()} zum Abrufen aller Bücher.</li>
 * <li>{@code void deleteById(Long id)} zum Löschen eines Buches anhand seiner ID.</li>
 * <li>{@code long count()} zur Ermittlung der Gesamtzahl der Bücher.</li>
 * <li>{@code boolean existsById(Long id)} zur Überprüfung, ob ein Buch mit einer bestimmten ID existiert.</li>
 * </ul>
 *
 * Zusätzlich zu diesen geerbten Methoden können hier spezifische, benutzerdefinierte Abfragemethoden
 * (wie {@code findByIsbn} oder {@code existsByIsbn}) definiert werden. Spring Data JPA generiert
 * die Implementierung für diese Methoden automatisch basierend auf ihren Namen.
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

    // Hier können wir weitere benutzerdefinierte Abfragen hinzufügen, z.B.:
    // List<Book> findByGenre(String genre);
    // List<Book> findByStatus(ReadingStatus status);
    // List<Book> findByAuthor(String author);
}
