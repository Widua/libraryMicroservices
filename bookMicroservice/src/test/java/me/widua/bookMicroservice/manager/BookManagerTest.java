package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.BookMicroserviceApplication;
import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.models.types.BookType;
import me.widua.bookMicroservice.repositories.BookRepository;
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

import static org.junit.jupiter.api.Assertions.*;
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
    BookModel exampleSingleBook ;


    @BeforeEach
    public void setUp(){
        underTest = new BookManager(repository);
        exampleBooks = Arrays.asList(
                new BookModel(
                        1,
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "7008004567" ,
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
        exampleSingleBook = new BookModel(
                1,
                "J. R. R. Tolkien",
                "The Hobbit, Part One",
                "9099099090",
                "First part of one of the most popular Tolkien book series",
                BookType.PHYSICAL,
                20
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
        String isbn = "9009001200";
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
        invalid.setISBN("7008004567");
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
        Optional<List<BookModel>> optionalResponse = Optional.of( (List<BookModel>) response.getBody() )  ;
        //Then
        assertEquals(HttpStatus.OK,response.getStatus());
        assertEquals(2 , optionalResponse.get().size());
    }

    @Test
    public void tryUpdateNonExistingBookByIsbn(){
        //Given
        final String isbn = exampleSingleBook.getISBN();
        exampleSingleBook.setInStorage(55);
        //When
        when( repository.getBookModelByISBN(isbn) ).thenReturn(Optional.empty());
        ResponseModel response = underTest.updateBook(exampleSingleBook,isbn);
        //Then
        assertEquals( HttpStatus.BAD_REQUEST , response.getStatus() );
        assertEquals( String.format("Book with ISBN: %s does not exist!",isbn) , response.getBody() );
    }

    @Test
    public void tryUpdateBookByIsbnWithInvalidIsbn(){
        //Given
        final String isbn = null;
        exampleSingleBook.setInStorage(55);
        //When
        ResponseModel response = underTest.updateBook(exampleSingleBook,isbn);
        //Then
        assertEquals( HttpStatus.BAD_REQUEST , response.getStatus() );
        assertEquals( String.format("ISBN: %s is not valid!",isbn) , response.getBody() );
    }

    @Test
    public void tryUpdateBookByIsbn(){
        //Given
        final String isbn = exampleSingleBook.getISBN();
        BookModel newBook = exampleSingleBook;
        newBook.setInStorage(56);
        newBook.setBookTitle("The Hobbit - Part One");
        //When
        when(repository.getBookModelByISBN(isbn)).thenReturn( Optional.of(exampleSingleBook) );
        ResponseModel response = underTest.updateBook(newBook,isbn);
        //Then
        assertAll(
                "Response properties test",
                () -> assertEquals(HttpStatus.OK , response.getStatus()),
                () -> assertEquals("Book successfully updated!", response.getBody())
        );
        verify(repository.save(newBook));
    }

    @Test
    public void tryUpdateNonExistingBookById(){
        //Given
        final Integer id = exampleSingleBook.getId();
        exampleSingleBook.setInStorage(55);
        //When
        when( repository.findById(id)).thenReturn(Optional.empty());
        ResponseModel response = underTest.updateBook(exampleSingleBook,id);
        //Then
        assertEquals( HttpStatus.BAD_REQUEST , response.getStatus() );
        assertEquals( String.format("Book with id: %s does not exist!",id) , response.getBody() );
    }

    @Test
    public void tryUpdateBookByIsbnWithInvalidId(){
        //Given
        final Integer id = null;
        exampleSingleBook.setInStorage(55);
        //When
        ResponseModel response = underTest.updateBook(exampleSingleBook, id);
        //Then
        assertEquals( HttpStatus.BAD_REQUEST , response.getStatus() );
        assertEquals( String.format("ISBN: %s is not valid!", id) , response.getBody() );
    }

    @Test
    public void tryUpdateBookById(){
        //Given
        final Integer id = exampleSingleBook.getId();
        BookModel newBook = exampleSingleBook;
        newBook.setInStorage(56);
        newBook.setBookTitle("The Hobbit - Part One");
        //When
        when(repository.findById(id)).thenReturn( Optional.of(exampleSingleBook) );
        ResponseModel response = underTest.updateBook(newBook, id);
        //Then
        assertAll(
                "Response properties test",
                () -> assertEquals(HttpStatus.OK , response.getStatus()),
                () -> assertEquals("Book successfully updated!", response.getBody())
        );
        verify(repository.save(newBook));
    }
}
