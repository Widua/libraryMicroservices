package me.widua.bookMicroservice.repositories;

import me.widua.bookMicroservice.models.BookModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookModel, Integer> {
    Optional<BookModel> getBookModelByBookTitle(String bookTitle);
    Optional<BookModel> getBookModelByISBN(String isbn);
    List<BookModel> getBookModelsByAuthor(String author);


}
