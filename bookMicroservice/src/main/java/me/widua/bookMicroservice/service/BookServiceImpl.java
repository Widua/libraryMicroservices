package me.widua.bookMicroservice.service;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.assertj.core.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl {

    private final BookRepository repository;

    @Autowired
    public BookServiceImpl(BookRepository repository){
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
        boolean doesIsbnDoesntExistInDb = !doesIsbnExistInDatabase(book.getISBN());
        boolean isIsbnValid = isISBNValid(book.getISBN());
        if ( isIsbnValid && doesIsbnDoesntExistInDb ){
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
                    if (!isISBNValid(book.getISBN()) || doesIsbnExistInDatabase(book.getISBN())){
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

    public boolean doesIsbnExistInDatabase(String isbn){
        if (isbn == null) return false;
        return repository.getBookModelByISBN(isbn).isPresent();
    }

    /* Simplified ISBN validation, on production regex can be switched to:
     *  (?:ISBN(?:-13)?:?\ )?(?=[0-9]{13}$|(?=(?:[0-9]+[-\ ]){4})[-\ 0-9]{17}$)97[89][-\ ]?[0-9]{1,5}[-\ ]?[0-9]+[-\ ]?[0-9]+[-\ ]?[0-9]
     *   source: https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s13.html
     */
    public boolean isISBNValid(String isbn){
        if (isbn == null) return false;
        String isbnRegex = "(?=[0-9]*$)(?:.{10}|.{13})";
        return isbn.matches(isbnRegex);
    }

    public ResponseModel updateBook(BookModel newBook, String isbn){

        if (!isISBNValid(isbn)){
            return ResponseModel.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("ISBN: %s is not valid!",isbn))
                    .build();
        }

        if (!doesIsbnExistInDatabase(isbn)){
            return ResponseModel.builder().status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Book with ISBN: %s does not exist!",isbn))
                    .build();
        }
        BookModel oldBook = repository.getBookModelByISBN(isbn).get();
        repository.save( prepareBookToUpdate(oldBook,newBook) );
        return ResponseModel
                .builder()
                .status(HttpStatus.OK)
                .body("Book successfully updated!")
                .build();
    }

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

    }
