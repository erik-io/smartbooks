package de.albbw.smartbooks.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.model.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class XmlImportService {
    private final BookService bookService;
    private final String XML_FILE_PATH = "/Buecher.xml";

    @Autowired
    XmlImportService(BookService bookService) {
        this.bookService = bookService;
    }

    public void importXmlFile() {
        XmlMapper xmlMapper = new XmlMapper();
        try (InputStream xmlStream = new ClassPathResource(XML_FILE_PATH).getInputStream()) {
            List<Book> listOfBooks = xmlMapper.readValue(xmlStream, xmlMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
            bookService.processAndSaveImportedBooks(listOfBooks, DataSource.XML);
        } catch (IOException e) {
            log.error("Error while reading XML file: {}", e.getMessage());
        }
    }
}
