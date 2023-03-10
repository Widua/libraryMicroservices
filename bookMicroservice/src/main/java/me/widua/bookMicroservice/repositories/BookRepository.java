package me.widua.bookMicroservice.repositories;

import me.widua.bookMicroservice.models.BookModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookModel, Integer> {
    Optional<List<BookModel>> getBookModelByBookTitle(String bookTitle);
    Optional<BookModel> getBookModelByISBN(String isbn);
    Optional<List<BookModel>> getBookModelsByAuthor(String author);


}
