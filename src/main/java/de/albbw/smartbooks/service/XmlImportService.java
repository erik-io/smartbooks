package de.albbw.smartbooks.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Service-Klasse, die für den Import von XML-Dateien verantwortlich ist.
 * Die importierten Daten werden in eine Liste von Büchern konvertiert und anschließend
 * durch den {@link BookService} verarbeitet und gespeichert.
 */
@Service
@Slf4j
public class XmlImportService {
    private final BookService bookService;

    @Autowired
    XmlImportService(BookService bookService) {
        this.bookService = bookService;
    }

    public void importXmlFile(InputStream xmlStream) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        
        // Mit `TypeFactory` und `constructCollectionType` wird festgelegt, dass der XML-Mapper die XML-Daten in eine Liste konvertiert, deren Elemente vom Typ Book sind.
        List<Book> listOfBooks = xmlMapper.readValue(xmlStream, xmlMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        bookService.processAndSaveImportedBooks(listOfBooks, DataSource.XML);
    }
}
