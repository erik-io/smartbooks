package de.albbw.smartbooks.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Die Klasse Book beschreibt ein Buchobjekt mit verschiedenen Attributen sowie Methoden
 * zum Arbeiten mit diesen Attributen.
 * <p>
 * Diese Klasse wird mit JPA-Annotations verwendet, um eine Entität für
 * die persistente Speicherung eines Buches in einer relationalen Datenbank zu definieren.
 */
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

    public Book() {
    }

    public Book(Long id, String isbn, String title, String author, String genre, Integer publicationYear, String publisher, Integer pageCount, String coverImageUrl, ReadingStatus status, DataSource source, LocalDateTime apiCheckTimestamp, LocalDateTime apiDataUpdateTimestamp) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.pageCount = pageCount;
        this.coverImageUrl = coverImageUrl;
        this.status = status;
        this.source = source;
        this.apiCheckTimestamp = apiCheckTimestamp;
        this.apiDataUpdateTimestamp = apiDataUpdateTimestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public ReadingStatus getStatus() {
        return status;
    }

    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public LocalDateTime getApiCheckTimestamp() {
        return apiCheckTimestamp;
    }

    public void setApiCheckTimestamp(LocalDateTime apiCheckTimestamp) {
        this.apiCheckTimestamp = apiCheckTimestamp;
    }

    public LocalDateTime getApiDataUpdateTimestamp() {
        return apiDataUpdateTimestamp;
    }

    public void setApiDataUpdateTimestamp(LocalDateTime apiDataUpdateTimestamp) {
        this.apiDataUpdateTimestamp = apiDataUpdateTimestamp;
    }

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
