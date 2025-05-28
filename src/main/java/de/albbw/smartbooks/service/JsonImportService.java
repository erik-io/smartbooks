package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import de.albbw.smartbooks.model.ReadingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Diese Service-Klasse ermöglicht das Importieren von Buchdaten aus einer JSON-Datei
 * und deren Speicherung in einer Datenbank. Die JSON-Datei wird aus dem Klassenpfad
 * geladen und verarbeitet, um Buchdaten zu extrahieren.
 * <p>
 * Methoden:
 * - importJsonFile(): Liest die JSON-Datei ein, filtert und validiert die Buchdaten
 * und speichert neue Bücher in der Datenbank, während Duplikate übersprungen werden.
 * <p>
 * Die Methode überprüft zudem, ob ein Buch basierend auf der ISBN bereits existiert.
 * Bereits vorhandene Bücher werden nicht erneut importiert. Für jedes Buch wird
 * ein Importprotokoll erstellt.
 * <p>
 * Fehlerbehandlung:
 * - Handhabt mögliche IOExceptions, die beim Lesen der JSON-Datei auftreten können.
 * - Behandelt generische Ausnahmen während der Verarbeitung.
 * <p>
 * Anforderungen:
 * - Die JSON-Datei muss ein Array von Büchern unter dem Node "buecher" enthalten.
 * - Die Buchdaten müssen im kompatiblen Format vorliegen.
 * <p>
 * Einschränkungen:
 * - Bücher ohne ISBN werden nicht importiert.
 * - Der JSON-Importpfad ist auf "/Buecher.json" festgelegt.
 */
@Slf4j
@Service
public class JsonImportService {
    private final BookService bookService;

    @Autowired
    public JsonImportService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Importiert Buchdaten aus einer JSON-Datei und speichert neue Bücher in der Datenbank.
     * Die Methode überprüft, ob eine Buchliste im JSON-Format unter dem Node "buecher" vorhanden ist,
     * und bearbeitet diese Einträge entsprechend.
     * <p>
     * Import-Logik:
     * - Bücher ohne ISBN werden übersprungen, da die ISBN essentiell für die Identifikation ist.
     * - Existierende Bücher (basierend auf der ISBN) werden nicht erneut importiert.
     * - Neue Bücher werden in die Datenbank gespeichert und mit der Quelle "JSON" markiert.
     * <p>
     * Fehlerbehandlung:
     * - IOExceptions, die beim Lesen der JSON-Datei auftreten, werden geloggt.
     * - Allgemeine Ausnahmen, die während des Imports auftreten, werden ebenfalls geloggt.
     * <p>
     * Voraussetzungen:
     * - Die JSON-Datei muss ein Array von Büchern unter dem "buecher"-Node enthalten.
     * <p>
     * Einschränkungen:
     * - Die Importlogik basiert stark auf der ISBN zur Vermeidung von Duplikaten.
     * - Bücher ohne ISBN oder unvollständige Daten werden ignoriert.
     * <p>
     * Pfad zur JSON-Datei:
     * - Die Datei wird aus dem Klassenpfad unter dem festgelegten Pfad "/Buecher.json" geladen.
     */
    public void importJsonFile(InputStream jsonStream) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper(); // Äquivalent zum CsvMapper für JSON

        // Liest die gesamte JSON-Struktur als Baum von JsonNode-Objekten ein.
        // Aufbau der JSON-Datenstruktur: { "buecher": [{...}, {...}, ...]" }
        JsonNode rootNode = jsonMapper.readTree(jsonStream);
        JsonNode booksNode = rootNode.path("buecher"); // Navigiert zum "buecher"-Array innerhalb des JSON-Dokuments.

        // Konvertiert den JsonNode in eine Liste von Buch-Objekten.
        // jsonMapper.getTypeFactory().constructCollectionType(List.class, Book.class) erstellt die notwendige Typinformation (List<Book>) für Jackson.
        List<Book> listOfBooks = jsonMapper.treeToValue(booksNode, jsonMapper.getTypeFactory().constructCollectionType(List.class, Book.class));

        for (Book book : listOfBooks) {
            if (book.getStatus() == null) {
                book.setStatus(ReadingStatus.UNKNOWN); // Standardwert setzen
            }
        }
        bookService.processAndSaveImportedBooks(listOfBooks, DataSource.JSON);

    }
}
