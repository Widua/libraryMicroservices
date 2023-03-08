package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.BookMicroserviceApplication;
import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.models.types.BookType;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BookMicroserviceApplication.class
)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
@Transactional
class BookManagerTest {
    private BookManager underTest ;
    @Mock
    private BookRepository repository ;

    List<BookModel> exampleBooks ;
    BookModel exampleInvalidBook ;


    @BeforeEach
    public void setUp(){
        underTest = new BookManager(repository);
        exampleBooks = Arrays.asList(
                new BookModel(
                        1,
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "70080045670" ,
                        "First book of Harry Potter adventures" ,
                        BookType.PHYSICAL ,
                        15),
                new BookModel(
                        2,
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "1001002003" ,
                        "First book of Harry Potter adventures" ,
                        BookType.E_BOOK ,
                        15),
                new BookModel(
                        3,
                        "Dante Alighieri",
                        "Divine comedy" ,
                        "9009008500" ,
                        "Classic of literature" ,
                        BookType.PHYSICAL ,
                        15)
        );

        exampleInvalidBook = new BookModel(
                1,
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                null ,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15
                );

    }

    @Test
    public void canGetAllBooks(){
        //When
        underTest.getBooks();
        //Ten
        verify(repository).findAll();
    }
    @Test
    public void noBooksGivesNoContent(){
        //When
        when(repository.findAll()).thenReturn(new ArrayList<>());
        ResponseModel response = underTest.getBooks();
        //Then
        assertEquals(response.getStatus(),HttpStatus.NO_CONTENT);
    }

    @Test
    public void canGetBookById(){
        //given
        Integer id = 1;
        //When
        underTest.getBook(id);
        //Then
        verify(repository).findById(id);
    }

    @Test
    public void noBookByIdGivesNoContent(){
        //Given
        Integer id = 1;
        //When
        when(repository.findById(id)).thenReturn(Optional.empty());
        ResponseModel response = underTest.getBook(id);
        //Then
        assertEquals(HttpStatus.NO_CONTENT , response.getStatus());
    }

    @Test
    public void canGetBookByIsbn(){
        //Given
        String isbn = "5006001200";
        //When
        underTest.getBookByISBN(isbn);
        //Then
        verify(repository).getBookModelByISBN(isbn);
    }

    @Test
    public void noBookByIsbnNoContent(){
        //Given
        String isbn = "5006001200";
        //When
        when(repository.getBookModelByISBN(isbn)).thenReturn(Optional.empty());
        ResponseModel response = underTest.getBookByISBN(isbn);
        //Then
        assertEquals(HttpStatus.NO_CONTENT , response.getStatus());
    }

    @Test
    public void addBookWithValidIsbn(){
        //Given
        BookModel toSave = exampleBooks.get(0);
        //When
        when(repository.getBookModelByISBN(toSave.getISBN())).thenReturn(Optional.empty());
        ResponseModel response = underTest.addBook(toSave);
        //Then
        verify(repository).save(toSave);
        assertEquals(HttpStatus.CREATED , response.getStatus());
    }

    @Test
    public void addBookWithInvalidIsbn(){
        //Given
        String isbn = "90090012005";
        BookModel toSave = new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                 isbn,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        //When
        when(repository.getBookModelByISBN(isbn)).thenReturn(Optional.of(toSave));
        ResponseModel responseModel = underTest.addBook(toSave);
        //Then
        assertEquals(responseModel.getStatus() , HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addBookWithNullIsbn(){
        //Given
        String isbn = null;
        BookModel toSave = new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                isbn,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        //When
        ResponseModel responseModel = underTest.addBook(toSave);
        //Then
        assertEquals(responseModel.getStatus() , HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addValidBooks(){
        //Given
        List<BookModel> toSave = exampleBooks;
        //When
        underTest.addBooks(toSave);
        //Then
        verify(repository).saveAll(toSave);
    }

    @Test
    public void tryAddBooksWithDuplicatedIsbn(){
        //Given
        ArrayList<BookModel> toSave = new ArrayList<>(exampleBooks);
        BookModel invalid = exampleInvalidBook;
        invalid.setISBN("70080045670");
        toSave.add(invalid);
        //When
        ResponseModel response = underTest.addBooks(toSave);
        //Then
        assertEquals(HttpStatus.BAD_REQUEST , response.getStatus());
        assertEquals("You provided at least two books with same ISBN! ISBN for each book must be unique!" , response.getBody());
    }

    @Test
    public void tryAddBooksWithISBNThatExistInDatabase(){
        //Given
        final String isbn = "1001002003";
        List<BookModel> toSave = exampleBooks;
        //When
        when( repository.getBookModelByISBN( anyString() )).thenReturn( Optional.empty() );
        when( repository.getBookModelByISBN( eq(isbn) )).thenReturn(Optional.of(exampleBooks.get(1)));
        ResponseModel response = underTest.addBooks(toSave);
        //Then
        assertEquals(HttpStatus.BAD_REQUEST , response.getStatus() );
        assertEquals("Adding stopped, because book in 1 index exist in database!",response.getBody());
    }

    @Test
    public void tryGetBooksByAuthor(){
        //Given
        final String author = "J.K. Rowling";
        //When
        when(repository.getBookModelsByAuthor(author))
                .thenReturn(
                        Optional.of(exampleBooks
                                .stream()
                                .filter(
                                        book -> {
                                            return book.getAuthor().equals(author);
                                        }).toList())
                );

        ResponseModel response = underTest.getBooksByAuthor(author);
        Optional<List<BookModel>> optionalResponse = Optional.of((List<BookModel>) response.getBody()) ;
        //Then
        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(2 , optionalResponse.get().size());
    }



}
