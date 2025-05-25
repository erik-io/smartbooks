package de.albbw.smartbooks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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
@ToString // Lombok Generiert die toString()-Methode
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok generiert equals() und hashCode()
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include // Nur 'isbn' für equals() und hashCode() verwenden
    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    @JsonProperty("titel")
    private String title;

    @JsonProperty("autor")
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


}
