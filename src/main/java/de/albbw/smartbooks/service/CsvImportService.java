package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Der CsvImportService ist eine Service-Klasse, die für den Import von Buchdaten
 * aus einer CSV-Datei zuständig ist. Die importierten Daten werden verarbeitet und
 * über den {@link BookService} gespeichert.
 */
@Slf4j
@Service
public class CsvImportService {
    private final BookService bookService;

    @Autowired
    public CsvImportService(BookService bookService) {
        this.bookService = bookService;
    }

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
