package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.assertj.core.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookManager {

    private final BookRepository repository;

    @Autowired
    public BookManager(BookRepository repository){
        this.repository = repository ;
    }

    public ResponseModel getBooks(){

        List<BookModel> books = (List<BookModel>) repository.findAll();

        if (books.size() > 0){
            return ResponseModel.builder().status(HttpStatus.OK).body(books).build();
        } else {
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();
        }
    }

    public ResponseModel getBook(Integer id){
        Optional<BookModel> book = repository.findById(id);
        if (book.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseModel.builder().status(HttpStatus.OK).body(book).build();
    }

    public ResponseModel getBookByISBN(String isbn){
        Optional<BookModel> queriedBookFromDb = repository.getBookModelByISBN(isbn);
        if (queriedBookFromDb.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseModel.builder().status(HttpStatus.OK).body(queriedBookFromDb.get()).build();
    }

    public ResponseModel addBook(BookModel book){
        if (isISBNValid(book.getISBN())){
            repository.save(book);
            return ResponseModel.builder().status(HttpStatus.CREATED).body(URI.create(String.format("/book/%s",book.getId()))).build();
        }
        return ResponseModel.builder().status(HttpStatus.BAD_REQUEST).body(String.format("The ISBN number %s is used by other book!", book.getISBN())).build();
    }

    public ResponseModel addBooks(List<BookModel> books){
        int size = books.size();
        AtomicInteger errorIndex = new AtomicInteger(0);
        AtomicBoolean isValid = new AtomicBoolean(true);

        Set<String> setOfIsbn = Streams
                .stream(books)
                .map(BookModel::getISBN)
                .collect(Collectors.toSet());

        if (setOfIsbn.size() != size){
            return ResponseModel.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .body("You provided at least two books with same ISBN! ISBN for each book must be unique!").build();
        }

        books.forEach(
                book -> {
                    if (!isISBNValid(book.getISBN())){
                        isValid.set(false);
                        errorIndex.set(books.indexOf(book));
                        return;
                    }
                }
        );

        if (isValid.get()){
            repository.saveAll(books);
            return ResponseModel.builder().status(HttpStatus.CREATED).body(String.format("Total number of created books %s",size)).build();
        }
        return ResponseModel
                .builder()
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        String.format("Adding stopped, because book in %s index exist in database!"
                                , errorIndex.get()))
                .build();
    }

    public ResponseModel getBooksByAuthor(String author) {
        Optional<List<BookModel>> books = repository.getBookModelsByAuthor(author);
        if (books.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseModel.builder().status(HttpStatus.OK).body(books.get()).build();
    }

    public boolean doesIsbnTakenInDb(String isbn){
        // TODO
    }

    public boolean isISBNValid(String isbn){
        //TODO
    }

    public ResponseModel updateBook(BookModel newBook, String isbn){
        // TODO
    }

    public ResponseModel updateBook(BookModel newBook, Integer id){
        // TODO
    }

}
