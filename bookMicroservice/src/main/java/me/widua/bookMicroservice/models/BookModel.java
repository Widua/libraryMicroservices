package me.widua.bookMicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.widua.bookMicroservice.models.types.BookType;

@Entity
@Getter
@Setter
public class BookModel {

    @Id
    @NotNull
    private Integer id;
    private String bookTitle;
    private String ISBN ;
    private String bookDescription;
    private BookType bookType;
    private Integer inStorage;

    public BookModel(Integer id, String bookTitle, String ISBN, String bookDescription, BookType bookType, Integer inStorage) {
        this.id = id;
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
