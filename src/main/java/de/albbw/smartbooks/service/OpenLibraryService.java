package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.albbw.smartbooks.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class OpenLibraryService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenLibraryService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public Optional<Book> fetchBookDetails(String isbn) {
        try {
            String jsonResponse = restClient.get()
                    .uri("/books?bibkeys=ISBN:{isbn}&jscmd=data&format=json")
                    .retrieve()
                    .body(String.class);

            log.info("Response from Open Library API for ISBN {}: {}", isbn, jsonResponse);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                log.warn("No response from Open Library API for ISBN {}", isbn);
                return Optional.empty();
            }

            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode bookNode = rootNode.path("ISBN:" + isbn);

            if (bookNode == null) {
                log.warn("No book found in Open Library API for ISBN {}", isbn);
                return Optional.empty();
            }

            Book book = new Book();
            book.setAuthor(extractFirstFromArray(bookNode, "authors"));
            book.setTitle(bookNode.get("title").asText());
            book.setPublicationYear(bookNode.has("publish_date") ? bookNode.get("publish_date").asInt() : null);
            book.setPublisher(extractFirstFromArray(bookNode, "publishers"));
            book.setPageCount(bookNode.has("number_of_pages") ? bookNode.get("number_of_pages").asInt() : null);

            if (bookNode.has("cover")) {
                book.setCoverImageUrl(bookNode.get("cover").get("large").asText());
            }

            return Optional.of(book);
        } catch (IOException e) {
            log.error("Error while reading JSON response from Open Library API: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error while fetching book details from Open Library API: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private String extractFirstFromArray(JsonNode parentNode, String fieldName) {
        if (parentNode.has(fieldName) && parentNode.get(fieldName).isArray()) {
            JsonNode firstNode = parentNode.get(fieldName).get(0);
            if (firstNode.has("name")) {
                return firstNode.get("name").asText();
            }
        }
        return null;
    }
}
