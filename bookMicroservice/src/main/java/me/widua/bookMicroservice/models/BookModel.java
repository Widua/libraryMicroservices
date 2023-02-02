package me.widua.bookMicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import me.widua.bookMicroservice.models.types.BookType;

@Entity
@Getter
@Setter
public class BookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String author ;
    private String bookTitle;
    private String ISBN ;
    private String bookDescription;
    private BookType bookType;
    private Integer inStorage;

    public BookModel(Integer id, String author , String bookTitle, String ISBN, String bookDescription, BookType bookType, Integer inStorage) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.ISBN = ISBN;
        this.bookDescription = bookDescription;
        this.bookType = bookType;
        this.inStorage = inStorage;
        this.author = author;
    }

    public BookModel(String author, String bookTitle, String ISBN, String bookDescription, BookType bookType, Integer inStorage) {
        this.author = author;
        this.bookTitle = bookTitle;
        this.ISBN = ISBN;
        this.bookDescription = bookDescription;
        this.bookType = bookType;
        this.inStorage = inStorage;
    }

    public BookModel() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookModel bookModel = (BookModel) o;

        return getId().equals(bookModel.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}