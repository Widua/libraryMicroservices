package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BookManager {

    private final BookRepository repository;

    @Autowired
    public BookManager(BookRepository repository){
        this.repository = repository ;
    }

    public ResponseEntity<Iterable<BookModel>> getBooks(){

        Iterable<BookModel> books = repository.findAll();

        if (getBooks().hasBody()){
            return ResponseEntity.ok(books);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
