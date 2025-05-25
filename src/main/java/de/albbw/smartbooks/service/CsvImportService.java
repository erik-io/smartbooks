package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.albbw.smartbooks.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static de.albbw.smartbooks.model.DataSource.CSV;

/**
 * Die Klasse CsvImportService dient zum Importieren von Büchern aus einer CSV-Datei
 * in die Datenbank mithilfe des {@link BookService}.
 * <p>
 * Diese Klasse liest eine konfigurierbare CSV-Datei ein, analysiert die Daten
 * und speichert sie als Buch-Objekte. Dabei werden doppelte Datensätze anhand
 * der ISBN-Felder identifiziert und ausgeschlossen.
 * <p>
 * Konstruktor:
 * - Ein {@link BookService}-Objekt wird benötigt, um Bücher in die Datenbank zu speichern
 * oder bestehende Bücher zu prüfen.
 * <p>
 * Methoden:
 * <p>
 * - {@code importCsvFile()}:
 * Liest die CSV-Datei ein, untersucht die Struktur und importiert die
 * darin enthaltenen Bücher. Die Methode validiert die Daten dahingehend,
 * dass eine ISBN vorhanden sein muss. Bücher mit fehlenden ISBNs oder
 * bereits existierenden Einträgen (basierend auf der ISBN) werden ausgelassen.
 * <p>
 * Fehlerbehandlung:
 * - Wenn die angegebene CSV-Datei nicht gelesen werden kann, wird eine
 * {@link IOException} abgefangen und eine Fehlermeldung ausgegeben.
 * <p>
 * Abhängigkeiten:
 * - {@link BookService}: Dient zur Kommunikation mit der Datenbank; speichert neue Bücher
 * und prüft auf vorhandene.
 * - {@link CsvMapper} und {@link CsvSchema}: Verwendet für die Verarbeitung
 * und Interpretation der CSV-Daten mittels Jackson.
 * - {@link ClassPathResource}: Dient zum Laden der CSV-Datei aus den Klassenpfad-Ressourcen.
 * <p>
 * Besonderheiten:
 * - Die Datenquelle der importierten Bücher wird als "CSV" gekennzeichnet.
 * - Bücher, die von unterschiedlichen Datenquellen importiert werden, erhalten
 * eine neue ID, um Konflikte zu vermeiden.
 */
@Slf4j
@Service
public class CsvImportService {
    private final BookService bookService;
    private final String CSV_FILE_PATH = "/Buecher.csv";

    @Autowired
    public CsvImportService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Importiert Buchdaten aus einer CSV-Datei und speichert sie in die Datenbank.
     * <p>
     * Die Methode analysiert und verarbeitet die CSV-Datei basierend auf einem
     * definierten Schema, das eine Kopfzeile und ein Komma als Trennzeichen vorgibt.
     * Dabei wird jede Zeile der CSV-Datei in ein Book-Objekt konvertiert und wie folgt behandelt:
     * <p>
     * 1. Wird keine ISBN angegeben, wird der entsprechende Datensatz übersprungen.
     * 2. Existiert ein Buch bereits in der Datenbank (basierend auf der ISBN), wird es nicht erneut importiert.
     * 3. Neu erkannte Bücher werden mit einer neu festgelegten ID und der Quelle "CSV" gespeichert.
     * <p>
     * Fehlerbehandlung:
     * - Bei Problemen beim Lesen der CSV-Datei (z. B. Datei nicht gefunden oder ungültiges Format) wird eine entsprechende
     * Fehlermeldung ausgegeben.
     * <p>
     * Voraussetzungen:
     * - Die CSV-Datei muss im Klassenpfad unter dem vorher definierten Dateipfad vorhanden sein.
     * - Die Methode erfordert Zugriff auf den BookService, um Datenbankoperationen durchzuführen.
     * <p>
     * Verwendete Frameworks und Bibliotheken:
     * - Jackson (CsvMapper, CsvSchema), um die CSV-Daten zu lesen und Book-Objekte zu erstellen.
     * - Spring (ClassPathResource), um die CSV-Datei aus den Klassenpfad-Ressourcen zu laden.
     * <p>
     * Hinweise:
     * - Die Methode verwendet ein try-with-resources-Konstrukt, um sicherzustellen, dass der InputStream geschlossen wird.
     * - Duplikate werden durch die Überprüfung der ISBN ermittelt.
     * - Die ID des Buches wird vor dem Speichern zurückgesetzt, um sicherzustellen, dass die Datenbank eine neue ID generiert.
     */
    public void importCsvFile() {
        CsvMapper csvMapper = new CsvMapper(); // Jackson-Objekt zum Lesen von CSV-Dateien

        // Schema für die CSV-Datei definieren
        // - mit Kopfzeile (withHeader)
        // - mit Komma als Trennzeichen (withColumnSeparator(','))
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');

        // try-with-resources, um den InputStream sicher zu schließen
        try (InputStream csvStream = new ClassPathResource(CSV_FILE_PATH).getInputStream()) {
            // Jackson liest die CSV-Daten und versucht sie in Book-Objekte umzuwandeln.
            MappingIterator<Book> bookMappingIterator = csvMapper.readerFor(Book.class).with(schema).readValues(csvStream);
            List<Book> listOfBooks = bookMappingIterator.readAll(); // Liste mit allen gelesenen Buch-Objekten
            for (Book book : listOfBooks) {
                // Überspringe Datensätze ohne ISBN, da diese für die Duplikatsprüfung essenziell ist
                if (book.getIsbn() == null) {
                    System.out.println(book.getTitle() + " has no ISBN. Skipping import of book.");
                    continue;
                }

                bookService.findByIsbn(book.getIsbn()).ifPresentOrElse(
                        // Lambda-Ausdruck für den Fall: Buch mit der gleichen ISBN existiert bereits.
                        existingBook -> {
                            System.out.println("\"" + book.getTitle() + "\" (ISBN: " + book.getIsbn() + ") already exists. Skipping import of book.");
                        },
                        // Lambda-Ausdruck für den Fall: Das Buch ist neu.
                        () -> {
                            book.setId(null); // Da wir mit heterogenen Datenquellen arbeiten, setzen wir die ID auf null, damit die DB eine neue ID generiert.
                            book.setSource(CSV); // Wir setzen die Datenquelle auf CSV
                            bookService.saveBook(book);
                            System.out.println("Importing " + book.getTitle() + " (ISBN: " + book.getIsbn() + ") into database.");
                        }
                );
            }
        } catch (IOException e) {
            log.error("Error while reading CSV file: {}", e.getMessage());
        }

    }
}
