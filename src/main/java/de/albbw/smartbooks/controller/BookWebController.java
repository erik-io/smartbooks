package de.albbw.smartbooks.controller;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller // Nicht @RestController für Thymeleaf-Views
@RequestMapping("web/books") // Eigener Basispfad für Webseiten, um Kollisionen mit /api/books zu vermeiden
public class BookWebController {
    private final BookService bookService;

    @Autowired
    public BookWebController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/list")
    public String getAllBooks(Model model) {
        List<Book> books = bookService.findAllBooks();
        model.addAttribute("books", books);  // Bücherliste dem Model hinzufügen
        return "book-table";
    }
}
