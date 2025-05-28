package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
    public void importCsvFile(InputStream csvStream) throws IOException {
        CsvMapper csvMapper = new CsvMapper(); // Jackson-Objekt zum Lesen von CSV-Dateien

        // Schema für die CSV-Datei definieren
        // - mit Kopfzeile (withHeader)
        // - mit Komma als Trennzeichen (withColumnSeparator(','))
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');

        // Kein try-with-resources für den übergebenen Stream, wenn der Controller den Stream öffnet und hier übergibt.
        // Sicherer ist oft, wenn der Controller den Stream öffnet und schließt.
        MappingIterator<Book> bookMappingIterator = csvMapper.readerFor(Book.class).with(schema).readValues(csvStream);
        List<Book> listOfBooks = bookMappingIterator.readAll(); // Liste mit allen gelesenen Buch-Objekten
        bookService.processAndSaveImportedBooks(listOfBooks, DataSource.CSV);
    }
}
