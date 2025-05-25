package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.albbw.smartbooks.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static de.albbw.smartbooks.model.DataSource.JSON;

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
@Service
public class JsonImportService {
    private final BookService bookService;
    private final String JSON_FILE_PATH = "/Buecher.json"; // Dateipfad zur JSON-Datei im Classpath (src/main/resources)

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
    public void importJsonFile() {
        ObjectMapper jsonMapper = new ObjectMapper(); // Äquivalent zum CsvMapper für JSON

        try (InputStream jsonStream = new ClassPathResource(JSON_FILE_PATH).getInputStream()) {
            // Liest die gesamte JSON-Struktur als Baum von JsonNode-Objekten ein.
            // Aufbau der JSON-Datenstruktur: { "buecher": [{...}, {...}, ...]" }
            JsonNode rootNode = jsonMapper.readTree(jsonStream);
            JsonNode booksNode = rootNode.path("buecher"); // Navigiert zum "buecher"-Array innerhalb des JSON-Dokuments.

            // Konvertiert den JsonNode in eine Liste von Buch-Objekten.
            // jsonMapper.getTypeFactory().constructCollectionType(List.class, Book.class) erstellt die notwendige Typinformation (List<Book>) für Jackson.
            List<Book> listOfBooks = jsonMapper.treeToValue(booksNode, jsonMapper.getTypeFactory().constructCollectionType(List.class, Book.class));

            for (Book book : listOfBooks) {
                if (book.getIsbn() == null) {
                    System.out.println(book.getTitle() + " has no ISBN. Skipping import of book.");
                    continue;
                }

                bookService.findByIsbn(book.getIsbn()).ifPresentOrElse(
                        existingBook -> {
                            System.out.println("\"" + book.getTitle() + "\" (ISBN: " + book.getIsbn() + ") already exists. Skipping import of book.");
                        },
                        () -> {
                            book.setId(null);
                            book.setSource(JSON);
                            bookService.saveBook(book);
                            System.out.println("Importing " + book.getTitle() + " (ISBN: " + book.getIsbn() + ") into database.");
                        }
                );
            }
        } catch (IOException e) {
            System.err.println("Error while reading JSON file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Falls andere unerwartete Fehler während des Imports auftreten
            System.err.println("An unexpected error occurred during JSON import: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
