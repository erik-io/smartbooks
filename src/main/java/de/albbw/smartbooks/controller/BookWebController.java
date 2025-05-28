package de.albbw.smartbooks.controller;

import de.albbw.smartbooks.model.Book;
import de.albbw.smartbooks.service.BookService;
import de.albbw.smartbooks.service.CsvImportService;
import de.albbw.smartbooks.service.JsonImportService;
import de.albbw.smartbooks.service.XmlImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Controller // Nicht @RestController für Thymeleaf-Views
@RequestMapping("web/books") // Eigener Basispfad für Webseiten, um Kollisionen mit /api/books zu vermeiden
public class BookWebController {
    private final BookService bookService;
    private final JsonImportService jsonImportService;
    private final CsvImportService csvImportService;
    private final XmlImportService xmlImportService;

    @Autowired
    public BookWebController(BookService bookService, JsonImportService jsonImportService, CsvImportService csvImportService, XmlImportService xmlImportService) {
        this.bookService = bookService;
        this.jsonImportService = jsonImportService;
        this.csvImportService = csvImportService;
        this.xmlImportService = xmlImportService;
    }

    @GetMapping("/list")
    public String getAllBooks(Model model) {
        List<Book> books = bookService.findAllBooks();
        model.addAttribute("books", books);  // Bücherliste dem Model hinzufügen
        return "book-table";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("fileType") String fileType,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("uploadMessage", "Please select a file to upload");
            redirectAttributes.addFlashAttribute("uploadStatus", "ERROR");
            return "redirect:/web/books/list";
        }

        try {
            InputStream inputStream = file.getInputStream();

            switch (fileType.toLowerCase()) {
                case "csv":
                    log.info("Importing CSV file...");
                    csvImportService.importCsvFile(inputStream);
                    break;
                case "json":
                    log.info("Importing JSON file...");
                    jsonImportService.importJsonFile(inputStream);
                    break;
                case "xml":
                    log.info("Importing XML file...");
                    xmlImportService.importXmlFile(inputStream);
                    break;
                default:
                    redirectAttributes.addFlashAttribute("uploadMessage", "Invalid file type");
                    redirectAttributes.addFlashAttribute("uploadStatus", "ERROR");
                    return "redirect:/web/books/list";
            }

            inputStream.close();
            redirectAttributes.addFlashAttribute("uploadMessage", file.getOriginalFilename() + " successfully uploaded!");
            redirectAttributes.addFlashAttribute("uploadStatus", "SUCCESS");
            return "redirect:/web/books/list";
        } catch (IOException e) {
            log.error("Error while reading uploaded file: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("uploadMessage", "Error while reading uploaded file: " + e.getMessage());
            redirectAttributes.addFlashAttribute("uploadStatus", "ERROR");
        } catch (Exception e) {
            log.error("An unexpected error occurred during file upload: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("uploadMessage", "An unexpected error occurred during file upload: " + e.getMessage());
        }

        return "redirect:/web/books/list";
    }
}
