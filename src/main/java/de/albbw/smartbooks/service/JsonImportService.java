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
 * Diese Klasse bietet eine Funktionalität, um Bücher aus einer JSON-Datei zu importieren.
 * Die importierten Bücher werden verarbeitet und in die Datenbank gespeichert.
 * <p>
 * Die JSON-Datenstruktur sollte wie folgt aufgebaut sein:
 * {
 * "buecher": [
 * {...},
 * {...}
 * ]
 * }
 * <p>
 * Eine Liste von Büchern wird aus dem JSON-Dokument extrahiert und in Objekte der Klasse {@link Book}
 * konvertiert. Falls der Status eines Buches nicht angegeben ist, wird der Standardstatus
 * {@code ReadingStatus.UNKNOWN} verwendet.
 * <p>
 * Abhängigkeiten:
 * - {@link BookService}: Zur Verarbeitung und Speicherung der importierten Bücher.
 */
@Slf4j
@Service
public class JsonImportService {
    private final BookService bookService;

    @Autowired
    public JsonImportService(BookService bookService) {
        this.bookService = bookService;
    }

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
