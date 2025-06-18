# SmartBooks: Intelligente Buchdaten-Plattform

Dieses Projekt wurde im Rahmen meiner Ausbildung zum Fachinformatiker für Anwendungsentwicklung entwickelt. Ziel war die Konzeption und Implementierung einer Spring Boot-Anwendung, die als zentrale Plattform für Buchinformationen dient. Die Anwendung kann Daten aus unterschiedlichen Formaten (CSV, JSON, XML) importieren, in einer MariaDB-Datenbank persistieren und über eine REST-Schnittstelle sowie eine einfache Weboberfläche zugänglich machen. Ein Kernfeature ist die Datenanreicherung durch die Anbindung der öffentlichen Open Library API.

### Architektur und Features

Die Anwendung folgt einer klassischen, service-orientierten Architektur unter Nutzung des Spring-Ökosystems.

-   **Datenimport aus heterogenen Quellen:** Implementierung von dedizierten Service-Klassen (`CsvImportService`, `JsonImportService`, `XmlImportService`), die mithilfe der Jackson-Bibliothek verschiedene Dateiformate parsen. Eine zentrale Methode im `BookService` verarbeitet die importierten Listen, verhindert Duplikate anhand der ISBN und speichert neue Datensätze transaktional in der Datenbank.
    
-   **Robuste REST-API:** Ein `BookController` stellt standardisierte CRUD-Endpunkte (`POST`, `GET`, `PUT`, `DELETE`) unter dem Pfad `/api/books` zur Verfügung. Die Fehlerbehandlung erfolgt über `ResponseEntity` mit aussagekräftigen HTTP-Statuscodes (z. B. 201 CREATED, 404 NOT FOUND, 409 CONFLICT), um eine klare und verlässliche Schnittstelle zu gewährleisten.
    
-   **Integration externer APIs:** Ein `OpenLibraryService` wurde implementiert, der über einen zentral konfigurierten `RestClient` mit der Open Library API kommuniziert. Dieser Service fragt basierend auf einer ISBN zusätzliche Buchdetails ab. Ein neuer REST-Endpunkt (`POST /{isbn}/fetch-api-data`) ermöglicht es, diesen Prozess gezielt anzustoßen.
    
-   **Web-Oberfläche mit Thymeleaf:** Ein `BookWebController` stellt eine serverseitig gerenderte Weboberfläche zur Verfügung. Diese zeigt die vorhandenen Bücher in einer tabellarischen Ansicht an und bietet eine Upload-Funktion für CSV-, JSON- und XML-Dateien, deren Typ automatisch anhand der Dateiendung erkannt wird.
    

### Technische Umsetzung und Herausforderungen

-   **Moderne Java-Entwicklung:** Das Projekt wurde mit Java 17 und Maven umgesetzt. Durch den Einsatz von Project Lombok wurde Boilerplate-Code (Getter, Setter, Konstruktoren) in den Model-Klassen signifikant reduziert.
    
-   **Herausforderung Daten-Parsing:** Eine besondere Herausforderung war die Verarbeitung uneinheitlicher Datumsformate aus der Open Library API. Dies wurde durch eine robuste, zweistufige Parsing-Logik gelöst: Zuerst wird versucht, das Datum mit einer Liste vordefinierter `DateTimeFormatter` zu parsen. Scheitert dies, greift ein Fallback-Mechanismus, der mittels eines regulären Ausdrucks das Jahr extrahiert.
    
-   **Dependency Management:** Die Jackson-Abhängigkeiten für CSV und XML mussten manuell zur `pom.xml` hinzugefügt werden, da sie nicht im Standardumfang des Spring Initializr enthalten sind. Dies vertiefte mein Verständnis für das Maven-Build-System.
    
-   **Datenbank-Management:** Die Anwendung nutzt Spring Data JPA mit Hibernate als Persistenz-Framework. Für die Entwicklung wurde die `ddl-auto=create-drop`-Strategie gewählt, um eine schnelle Iteration am Datenmodell zu ermöglichen.
    

### Wichtige Lernerfahrungen

Dieses Projekt war eine umfassende Übung im gesamten Backend-Entwicklungszyklus. Ich konnte meine Fähigkeiten in der Konzeption von REST-Schnittstellen, der Verarbeitung unterschiedlicher Datenformate und der sauberen Strukturierung von Code in Service-, Repository- und Controller-Schichten maßgeblich vertiefen. Besonders die systematische Herangehensweise an unvorhergesehene Probleme, wie die inkonsistenten API-Antworten, war eine wertvolle praktische Erfahrung.
