package de.albbw.smartbooks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Konfigurationsklasse für Anwendung.
 * Hier werden zentrale Beans definiert, die im Spring-Kontext verfügbar gemacht werden.
 */
@Configuration
public class AppConfig {


    /**
     * Erstellt und liefert eine konfigurierte Instanz eines RestClient.
     * Durch die Definition als Bean kann sie per Dependency Injection in anderen Komponenten (z. B. Services) verwendet werden
     *
     * @return eine Instanz von RestClient, die für die Open Library API konfiguriert ist.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://openlibrary.org/api")
                .build();
    }
}
