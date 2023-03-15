package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.repositories.BookRepository;
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
        if (isISBNValid(book.getISBN())){
            repository.save(book);
            return ResponseModel.builder().status(HttpStatus.CREATED).body(URI.create(String.format("/book/%s",book.getId()))).build();
        }
        return ResponseModel.builder().status(HttpStatus.BAD_REQUEST).body(String.format("The ISBN number %s is used by other book!", book.getISBN())).build();
    }

    public ResponseModel addBooks(Iterable<BookModel> books){
        AtomicInteger size = new AtomicInteger(0);
        AtomicBoolean valid = new AtomicBoolean(true);
        books.forEach(
                book -> {
                    if (isISBNValid(book.getISBN())){
                        size.addAndGet(1);
                    }else {
                        valid.set(false);
                        return;
                    }
                }
        );

        if (valid.get()){
            repository.saveAll(books);
            return ResponseModel.builder().status(HttpStatus.CREATED).body(String.format("Total number of created books %s",size.get())).build();
        }
        return ResponseModel.builder().status(HttpStatus.BAD_REQUEST).body(String.format("Adding stopped, because of bad ISBN in %s index", size)).build();
    }

    public ResponseModel getBooksByAuthor(String author) {
        Optional<List<BookModel>> books = repository.getBookModelsByAuthor(author);
        if (books.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.NO_CONTENT).build();

        }
        return ResponseModel.builder().status(HttpStatus.OK).body(books.get()).build();
    }

    public ResponseModel updateBook(BookModel newBook, String isbn, boolean nullAcceptance){

        if (  !isISBNValid(newBook.getISBN())  &&  !newBook.getISBN().equals(isbn)  ){
            return ResponseModel.builder().status(HttpStatus.BAD_REQUEST).body(String.format("ISBN: %s is not valid!",newBook.getISBN())).build();
        }

        Optional<BookModel> bookBeforeUpdate = repository.getBookModelByISBN(isbn) ;

        if (bookBeforeUpdate.isEmpty()){
            return ResponseModel.builder().status(HttpStatus.BAD_REQUEST).body(String.format("Book with ISBN: %s does not exist!", isbn)).build() ;
        }

        BookModel updatedBook = updateBookModel( bookBeforeUpdate.get() , newBook , nullAcceptance) ;
        repository.save(updatedBook);

        return ResponseModel.builder().status(HttpStatus.OK).body("Successfully updated book!").build();
    }

    public ResponseModel updateBook(BookModel newBook, Integer id, boolean nullAcceptance){


        return ResponseModel.builder().status(HttpStatus.OK).body("Successfully updated book!").build();
    }



    private boolean isISBNValid(String isbn){
        Set<String> setOfIsbn = new HashSet<>();
        repository.findAll().forEach(book -> {setOfIsbn.add(book.getISBN());});

        if (setOfIsbn.contains(isbn) && isbn == null){
            return false;
        }

        return true;
    }

    private BookModel updateBookModel(BookModel oldBook, BookModel newBook, boolean nullAcceptance){
        repository.deleteById(oldBook.getId());

        if (newBook.getISBN() != null) {
            oldBook.setISBN(newBook.getISBN());
        }

        if (newBook.getAuthor() != null || nullAcceptance){
            oldBook.setAuthor(newBook.getAuthor());
        }

        if (newBook.getBookDescription() != null || nullAcceptance){
            oldBook.setBookDescription(newBook.getBookDescription());
        }

        if (newBook.getBookType() != null || nullAcceptance){
            oldBook.setBookType(newBook.getBookType());
        }

        if (newBook.getBookTitle() != null || nullAcceptance){
            oldBook.setBookTitle(newBook.getBookTitle());
        }

        if (newBook.getInStorage() != null || nullAcceptance){
            oldBook.setInStorage(newBook.getInStorage());
        }

        return oldBook;
    }
}
