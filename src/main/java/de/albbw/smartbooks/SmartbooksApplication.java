package de.albbw.smartbooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartbooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartbooksApplication.class, args);

//        ConfigurableApplicationContext context = SpringApplication.run(SmartbooksApplication.class, args);

//        CsvImportService csvImportService = context.getBean(CsvImportService.class);
//        csvImportService.importCsvFile();

//        JsonImportService jsonImportService = context.getBean(JsonImportService.class);
//        jsonImportService.importJsonFile();

    }

}
