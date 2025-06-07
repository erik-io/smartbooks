package de.albbw.smartbooks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.albbw.smartbooks.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Die Klasse OpenLibraryService bietet Funktionen für die Kommunikation mit der Open Library API,
 * um Buchdetails anhand einer ISBN abzurufen. Sie verarbeitet die Antwort der API und gibt die
 * entsprechenden Buchinformationen in Form eines Book-Objekts zurück.
 * <p>
 * Sie verwendet die RestClient- und ObjectMapper-Bibliotheken, um Netzwerkanfragen zu senden und
 * die JSON-Antwort zu verarbeiten. Ergänzend beinhaltet die Klasse Mechanismen, um Publikationsdaten
 * zu parsen und Buchinformationen wie Titel, Autor, Verlag, Seitenanzahl und Cover-URL zu extrahieren.
 */
@Service
@Slf4j
public class OpenLibraryService {
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"), // Für "29.03.2019"
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH), // Für "Jun 15, 2012"
            DateTimeFormatter.ofPattern("yyyy-MM"),           // Für "1998-10"
            DateTimeFormatter.ofPattern("yyyy")               // Für "2009"
    );
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private final Pattern yearPattern = Pattern.compile("(\\d{4})");

    @Autowired
    public OpenLibraryService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }


    /**
     * Analysiert einen Datums-String und versucht, das Jahr daraus zu extrahieren.
     * Die Methode unterstützt mehrere Datumsformate und verwendet reguläre Ausdrücke
     * als Fallback, falls das Parsen mit vordefinierten Formaten fehlschlägt.
     *
     * @param dateStr Der zu analysierende Datums-String. Kann null oder leer sein.
     * @return Die extrahierte Jahreszahl als Integer, oder null, wenn das Datum ungültig oder nicht zu analysieren ist.
     */
    private Integer parseYearFromDateString(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                if (formatter.toString().contains("d)")) {
                    return LocalDate.parse(dateStr, formatter).getYear();
                } else if (formatter.toString().contains("M")) {
                    return YearMonth.parse(dateStr, formatter).getYear();
                } else {
                    return Year.parse(dateStr, formatter).getValue();
                }
            } catch (DateTimeParseException ignored) {

            }

            log.warn("Could not parse date string {} with standard formats. Falling back to regex.", dateStr);
            Matcher matcher = yearPattern.matcher(dateStr);
            if (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            } else {
                log.warn("Could not find year in date string {} with regex.", dateStr);
            }
        }
        log.warn("Could not extract a year from date string: '{}'", dateStr);
        return 0;
    }

    /**
     * Ruft Buchdetails von der Open Library API basierend auf der übergebenen ISBN ab.
     * Die Methode verarbeitet die API-Antwort, extrahiert relevante Informationen
     * (z. B. Titel, Autor, Verlag, Seitenanzahl) und gibt diese in Form eines optionalen
     * Book-Objekts zurück. Im Fehlerfall oder wenn keine Daten gefunden werden, wird ein
     * leeres Optional zurückgegeben.
     *
     * @param isbn Die ISBN des Buches, für das die Details abgerufen werden sollen.
     * @return Ein Optional mit den Buchdetails als Book-Objekt, oder ein leeres Optional,
     * falls keine Daten gefunden wurden oder ein Fehler auftrat.
     */
    public Optional<Book> fetchBookDetails(String isbn) {
        try {
            // Die API-Anfrage senden und die Antwort als String erhalten
            String jsonResponse = restClient.get()
                    .uri("/books?bibkeys=ISBN:{isbn}&jscmd=data&format=json", isbn)
                    .retrieve()
                    .body(String.class);

            log.info("Response from Open Library API for ISBN {}: {}", isbn, jsonResponse);

            // Prüfen ob die Antwort leer ist oder das Buch nicht gefunden wurde
            if (jsonResponse == null || jsonResponse.equals("{}")) {
                log.warn("No response from Open Library API for ISBN {}", isbn);
                return Optional.empty();
            }

            // Die JSON-Antwort verarbeiten
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode bookNode = rootNode.path("ISBN:" + isbn);

            if (bookNode.isMissingNode() || bookNode.isNull() || !bookNode.isObject() || bookNode.isEmpty()) {
                log.warn("No book found in Open Library API for ISBN {}", isbn);
                return Optional.empty();
            }

            // Ein neues Buch-Objekt erstellen und mit den API-Daten füllen
            // Genre setzen wir aufgrund der inkonsistenten Daten nicht
            Book book = new Book();
            book.setAuthor(extractFirstFromArray(bookNode, "authors"));
            book.setTitle(bookNode.get("title").asText());
            book.setPublisher(extractFirstFromArray(bookNode, "publishers"));
            book.setPageCount(bookNode.has("number_of_pages") ? bookNode.get("number_of_pages").asInt() : null);

            if (bookNode.has("publish_date")) {
                String dateStr = bookNode.get("publish_date").asText();
                book.setPublicationYear(parseYearFromDateString(dateStr));
            }

            // Cover-URL extrahieren (falls vorhanden)
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

    /**
     * Eine Hilfsmethode, um den ersten Wert aus einem JSON-Array zu extrahieren.
     * <p>
     * Wenn das angegebene Feld ein Array enthält und mindestens ein Element mit
     * einem "name"-Attribut vorhanden ist, wird der Wert dieses Attributs zurückgegeben.
     *
     * @param parentNode Das JSON-Knotenobjekt, das die zu analysierenden Daten enthält.
     * @param fieldName  Der Feldname, der ein Array im JSON-Knoten angibt.
     * @return Der erste Name im Array als String, oder null, falls das Feld nicht existiert,
     * kein Array ist, leer ist oder kein "name"-Attribut enthält.
     */
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
