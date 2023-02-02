package me.widua.bookMicroservice.api;

import me.widua.bookMicroservice.manager.BookManager;
import me.widua.bookMicroservice.models.BookModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class BookApi {
    private final BookManager manager ;
    @Autowired
    public BookApi (BookManager manager){
        this.manager = manager;
    }

    @GetMapping("/books")
    public ResponseEntity<?> getBooks(){
        return manager.getBooks();
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Integer id){
        return manager.getBook(id);
    }

    @PostMapping("/book")
    public ResponseEntity<?> addBook(@RequestBody BookModel book ){
        return manager.addBook(book);
    }

    @PostMapping("/books")
    public ResponseEntity<?> addBooks(@RequestBody Iterable<BookModel> books){
        return manager.addBooks(books);
    }

}
