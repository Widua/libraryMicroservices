package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BookManager {

    private final BookRepository repository;

    @Autowired
    public BookManager(BookRepository repository){
        this.repository = repository ;
    }

    public ResponseEntity<?> getBooks(){

        List<BookModel> books = (List<BookModel>) repository.findAll();

        if (books.size() > 0){
            return ResponseEntity.ok(books);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<?> getBook(Integer id){
        Optional<BookModel> book = repository.findById(id);
        if (book.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(book.get());
    }

    public ResponseEntity<?> addBook(BookModel book){
        if (isISBNValid(book.getISBN())){
            repository.save(book);
            return ResponseEntity.created(URI.create(String.format("/book/%s",book.getId()))).build();
        }
        return ResponseEntity.badRequest().body(String.format("The ISBN number %s is used by other book!", book.getISBN()));
    }

    public ResponseEntity<?> addBooks(Iterable<BookModel> books){
        AtomicInteger size = new AtomicInteger(0);
        AtomicBoolean valid = new AtomicBoolean(true);
        books.forEach(
                book -> {
                    if (isISBNValid(book.getISBN())){
                        repository.save(book);
                        size.addAndGet(1);
                    }else {
                        valid.set(false);
                        return;
                    }
                }
        );

        if (valid.get()){
            return ResponseEntity.status(HttpStatus.CREATED).body(String.format("Total number of created books %s",size.get()));
        }

        return ResponseEntity.badRequest().body(String.format("Adding stopped, because of bad ISBN in %s index", size));
    }



    public boolean isISBNValid(String isbn){
        Set<String> setOfIsbn = new HashSet<>();
        repository.findAll().forEach(book -> {setOfIsbn.add(book.getISBN());});

        if (setOfIsbn.contains(isbn)){
            return false;
        }

        return true;
    }

}
