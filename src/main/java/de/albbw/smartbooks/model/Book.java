package de.albbw.smartbooks.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;
    private String genre;
    private Integer publicationYear;
    private String publisher;
    private Integer pageCount;
    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    private ReadingStatus status; // READ, PLANNED, READING, NULL

    @Enumerated(EnumType.STRING)
    private DataSource source; // CSV, JSON, XML, API, UNKNOWN

    private LocalDateTime apiCheckTimestamp;
    private LocalDateTime apiDataUpdateTimestamp;

    public Book() {
    }

    public Book(Long id, String isbn, String title, String author, String genre, Integer publicationYear, String publisher, Integer pageCount, String coverImageUrl, ReadingStatus status, DataSource source, LocalDateTime apiCheckTimestamp, LocalDateTime apiDataUpdateTimestamp) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.publisher = publisher;
        this.pageCount = pageCount;
        this.coverImageUrl = coverImageUrl;
        this.status = status;
        this.source = source;
        this.apiCheckTimestamp = apiCheckTimestamp;
        this.apiDataUpdateTimestamp = apiDataUpdateTimestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public ReadingStatus getStatus() {
        return status;
    }

    public void setStatus(ReadingStatus status) {
        this.status = status;
    }

    public DataSource getSource() {
        return source;
    }

    public void setSource(DataSource source) {
        this.source = source;
    }

    public LocalDateTime getApiCheckTimestamp() {
        return apiCheckTimestamp;
    }

    public void setApiCheckTimestamp(LocalDateTime apiCheckTimestamp) {
        this.apiCheckTimestamp = apiCheckTimestamp;
    }

    public LocalDateTime getApiDataUpdateTimestamp() {
        return apiDataUpdateTimestamp;
    }

    public void setApiDataUpdateTimestamp(LocalDateTime apiDataUpdateTimestamp) {
        this.apiDataUpdateTimestamp = apiDataUpdateTimestamp;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", publicationYear=" + publicationYear +
                ", publisher='" + publisher + '\'' +
                ", pageCount=" + pageCount +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", status=" + status +
                ", source=" + source +
                ", apiCheckTimestamp=" + apiCheckTimestamp +
                ", apiDataUpdateTimestamp=" + apiDataUpdateTimestamp +
                '}';
    }
}
