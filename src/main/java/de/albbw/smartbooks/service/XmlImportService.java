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
        List<Book> listOfBooks = xmlMapper.readValue(xmlStream, xmlMapper.getTypeFactory().constructCollectionType(List.class, Book.class));
        bookService.processAndSaveImportedBooks(listOfBooks, DataSource.XML);
    }
}
