package de.albbw.smartbooks;

import de.albbw.smartbooks.service.CsvImportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SmartbooksApplication {

    public static void main(String[] args) {
        // SpringApplication.run(SmartbooksApplication.class, args);

        ConfigurableApplicationContext context = SpringApplication.run(SmartbooksApplication.class, args);
        CsvImportService csvImportService = context.getBean(CsvImportService.class);
        csvImportService.importCsvFile();
    }

}
