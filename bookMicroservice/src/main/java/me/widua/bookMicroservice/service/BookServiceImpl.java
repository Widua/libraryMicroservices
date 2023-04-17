package me.widua.bookMicroservice.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.models.exception.BookAlreadyExistException;
import me.widua.bookMicroservice.models.exception.BookNotExistException;
import me.widua.bookMicroservice.models.exception.InvalidIsbnException;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.*;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;

    @Autowired
    public BookServiceImpl(BookRepository repository){
        this.repository = repository ;
    }

    @Override
    public ResponseModel getBooks(){
        List<BookModel> books = (List<BookModel>) repository.findAll();

        if (books.size() > 0){
            return ResponseModel.builder().status(HttpStatus.OK).body(books).build();
        } else {
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();
        }
    }

    @Override
    public ResponseModel getBook(Integer id){
        Optional<BookModel> book = repository.findById(id);
        if (book.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseModel.builder().status(HttpStatus.OK).body(book).build();
    }


    @Override
    public ResponseModel getBook(String isbn){
        Optional<BookModel> queriedBookFromDb = repository.getBookModelByISBN(isbn);
        if (queriedBookFromDb.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseModel.builder().status(HttpStatus.OK).body(queriedBookFromDb.get()).build();
    }


    @Override
    public ResponseModel addBook(BookModel book){
        validateISBN(book.getISBN());
        thisBookShouldntExistInDatabase(book.getISBN());

        repository.save(book);
        return ResponseModel.builder().status(HttpStatus.CREATED).body(URI.create(String.format("/book/%s",book.getId()))).build();
    }

    @Override
    public ResponseModel addBooks(List<BookModel> books){
        URI uri = URI.create("");
        boolean isbnRepetitions = new HashSet<>(books).size() != books.size();

        if( isbnRepetitions ) {
            throw new IllegalArgumentException("There are duplicated ISBN values in input");
        }

        books.forEach(
                book -> {
                    validateISBN(book.getISBN());
                    thisBookShouldntExistInDatabase(book.getISBN());
                }
        );
        repository.saveAll(books);
        return ResponseModel.builder().status(HttpStatus.CREATED).body(uri).build();

    }

    @Override
    public ResponseModel getBooks(String author) {
        Optional<List<BookModel>> books = repository.getBookModelsByAuthor(author);
        if (books.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseModel.builder().status(HttpStatus.OK).body(books.get()).build();
    }

    @Override
    public ResponseModel updateBook(BookModel newBook, String isbn){
        validateISBN(isbn);
        thisBookShouldExistInDatabase(isbn);
        BookModel oldBook = repository.getBookModelByISBN(isbn).get();
        repository.save( prepareBookToUpdate(oldBook,newBook) );
        return ResponseModel
                .builder()
                .status(HttpStatus.OK)
                .body("Book successfully updated!")
                .build();
    }
    @Override
    public ResponseModel updateBook(BookModel newBook, Integer id){
        if (id == null){
            return ResponseModel.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .body("ID cannot be null!")
                    .build();
        }

        Optional<BookModel> oldBook = repository.findById(id) ;

        if (oldBook.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Book with id: %s does not exist!",id))
                    .build();
        }

        repository.save( prepareBookToUpdate(oldBook.get(),newBook) );
        return ResponseModel
                .builder()
                .status(HttpStatus.OK)
                .body("Book successfully updated!")
                .build();
    }

    public BookModel prepareBookToUpdate( BookModel oldBook , BookModel newBook ){
        if(newBook.getAuthor() != null){
            oldBook.setAuthor(newBook.getAuthor());
        }
        if (newBook.getBookTitle() != null){
            oldBook.setBookTitle(newBook.getBookTitle());
        }
        if (newBook.getBookDescription() != null){
            oldBook.setBookDescription(newBook.getBookDescription());
        }
        if (newBook.getBookType() != null){
            oldBook.setBookType(newBook.getBookType());
        }
        if (newBook.getInStorage() != null){
            oldBook.setInStorage(newBook.getInStorage());
        }
        return oldBook;
    }

    public void validateISBN(String isbn) throws InvalidIsbnException{
        if (isbn == null) throw new InvalidIsbnException("ISBN cannot be null.");
        String isbnRegex = "[0-9]{10}|[0-9]{13}";
        if (!isbn.matches(isbnRegex)){
            throw new InvalidIsbnException("Invalid ISBN");
        }
    }

    public void validateId(Integer id) throws IllegalArgumentException{
        if (id == null){
            throw new InvalidIsbnException("ID cannot be null");
        }
    }

    private boolean doesBookExistInDatabase(String isbn){
        return repository.existsBookModelByISBN(isbn);
    }

    private boolean doesBookExistInDatabase(Integer id){
        return repository.existsById(id);
    }

    public void thisBookShouldntExistInDatabase(String isbn) throws BookAlreadyExistException{
        if (doesBookExistInDatabase(isbn)){
            throw new BookAlreadyExistException( String.format("Book with ISBN: %s already exist.",isbn) );
        }
    }

    public void thisBookShouldntExistInDatabase(Integer id) throws BookAlreadyExistException{
        if (doesBookExistInDatabase(id)){
            throw new BookAlreadyExistException(String.format("Book with ID: %s already exist.", id));
        }
    }

    public void thisBookShouldExistInDatabase(String isbn) throws BookNotExistException{
        if (!doesBookExistInDatabase(isbn)){
            throw new BookNotExistException("Book with provided ISBN does not exist.");
        }
    }

    public void thisBookShouldExistInDatabase(Integer id) throws BookNotExistException{
        if (!doesBookExistInDatabase(id)){
            throw new BookNotExistException("Book with provided ID does not exist.");
        }
    }

    }
