package de.albbw.smartbooks.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Die Klasse Book beschreibt ein Buchobjekt mit verschiedenen Attributen sowie Methoden
 * zum Arbeiten mit diesen Attributen.
 * <p>
 * Diese Klasse wird mit JPA-Annotations verwendet, um eine Entität für
 * die persistente Speicherung eines Buches in einer relationalen Datenbank zu definieren.
 */
@Setter // Generiert Setter-Methoden für alle Felder
@Getter // Generiert Getter-Methoden für alle Felder
@NoArgsConstructor // Generiert einen Standardkonstruktor ohne Parameter
@AllArgsConstructor // Generiert einen Konstruktor mit allen Feldern als Parameter
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;
    private String genre;
    private Integer publicationYear; // Integer statt int, um null zuzulassen
    private String publisher;
    private Integer pageCount;
    private String coverImageUrl;

    /**
     * Der Status eines Lesevorgangs.
     * <p>
     * Dieser Wert gibt an, in welchem Zustand sich ein Lesevorgang befindet.
     * Mögliche Werte sind:
     * - READ: Das Lesen ist abgeschlossen.
     * - PLANNED: Das Lesen ist geplant, hat aber noch nicht begonnen.
     * - READING: Das Lesen ist aktuell im Gange.
     * - UNKNOWN: Kein Status definiert.
     */
    @Enumerated(EnumType.STRING)
    private ReadingStatus status;

    /**
     * Repräsentiert die Quelle der Daten, die verwendet werden.
     * Die möglichen Werte sind:
     * - CSV: Datenquelle ist eine CSV-Datei.
     * - JSON: Datenquelle ist eine JSON-Datei.
     * - XML: Datenquelle ist eine XML-Datei.
     * - API: Datenquelle ist eine API.
     * - UNKNOWN: Die Datenquelle ist unbekannt.
     */
    @Enumerated(EnumType.STRING)
    private DataSource source;

    /**
     * Speichert den Zeitstempel, zu dem die Überprüfung der API durchgeführt wurde.
     * Dieser Zeitstempel gibt an, wann die letzte Anfrage oder Überprüfung
     * der API erfolgte, und kann zur Nachverfolgung oder zeitlichen Steuerung
     * weiterer API-Interaktionen verwendet werden.
     */
    private LocalDateTime apiCheckTimestamp;

    /**
     * Speichert den Zeitstempel, wann die API-Daten zuletzt aktualisiert wurden.
     * Diese Variable dient zur Nachverfolgung der Aktualität der abgerufenen Daten.
     */
    private LocalDateTime apiDataUpdateTimestamp;

    /**
     * Gibt eine textuelle Darstellung des Buchobjekts zurück.
     *
     * @return Eine String-Darstellung des Buchobjekts, die die Werte der Eigenschaften
     * wie ID, ISBN, Titel, Autor, Genre, Veröffentlichungsjahr, Verlag,
     * Seitenanzahl, URL des Titelbildes, Status, Quelle sowie Zeitstempel
     * für API-Überprüfung und Datenaktualisierung enthält.
     */
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", publicationYear=" + publicationYear +
                ", publisher='" + publisher + '\'' +
                ", pageCount=" + pageCount +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", status=" + status +
                ", source=" + source +
                ", apiCheckTimestamp=" + apiCheckTimestamp +
                ", apiDataUpdateTimestamp=" + apiDataUpdateTimestamp +
                '}';
    }

    /**
     * Überprüft, ob dieses Objekt gleich dem angegebenen Objekt ist.
     *
     * @param o das zu vergleichende Objekt
     * @return true, wenn das angegebene Objekt gleich diesem Objekt ist, andernfalls false
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    /**
     * Berechnet den Hashcode für dieses Objekt basierend auf der ISBN.
     *
     * @return den Hashcode-Wert für dieses Objekt
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(isbn);
    }
}
