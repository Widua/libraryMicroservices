package me.widua.bookMicroservice.service;


import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import me.widua.bookMicroservice.models.exception.BookAlreadyExistException;
import me.widua.bookMicroservice.models.exception.BookNotExistException;
import me.widua.bookMicroservice.models.exception.InvalidIsbnException;
import me.widua.bookMicroservice.models.types.BookType;

import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class BookServiceImplTest {
    @Autowired
    private BookServiceImpl underTest ;
    @MockBean
    private BookRepository repository ;

    private List<BookModel> exampleBooks;
    private BookModel exampleInvalidBook;
    private BookModel exampleSingleBook;

    @BeforeEach
    public void setUp(){
        exampleBooks =  Arrays.asList(
                new BookModel(
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "5006001200" ,
                        "First book of Harry Potter adventures" ,
                        BookType.PHYSICAL ,
                        15),
                new BookModel(
                        2,
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "7008004567" ,
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
        underTest.getBook(isbn);
        //Then
        verify(repository).getBookModelByISBN(isbn);
    }

    @Test
    public void noBookByIsbnNoContent(){
        //Given
        String isbn = "5006001200";
        //When
        when(repository.getBookModelByISBN(isbn)).thenReturn(Optional.empty());
        ResponseModel response = underTest.getBooks(isbn);
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
    public void addBookWithDuplicatedIsbn(){
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
        //Then
        Exception ex = assertThrows( BookAlreadyExistException.class , () -> { underTest.addBook(toSave); } );
        assertEquals( String.format("Book with ISBN: %s already exist in database.",isbn) ,ex.getMessage() );
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
        //Then
        Exception ex = assertThrows(InvalidIsbnException.class , ()-> { underTest.addBook(toSave); });
        assertEquals("ISBN cannot be null.",ex.getMessage());
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
        when( repository.getBookModelByISBN(invalid.getISBN()) ).thenReturn( Optional.of(exampleInvalidBook) );
        //Then
        Exception ex = assertThrows( BookAlreadyExistException.class , () -> { underTest.addBooks(toSave); } );
        assertEquals( String.format("Book with ISBN: %s already exist in database.",invalid.getISBN()), ex.getMessage() );
    }

    @Test
    public void tryAddBooksWithISBNThatExistInDatabase(){
        //Given
        final String isbn = "7008004567";
        List<BookModel> toSave = exampleBooks;
        //When
        when( repository.getBookModelByISBN( anyString() )).thenReturn( Optional.empty() );
        when( repository.getBookModelByISBN( eq(isbn) )).thenReturn(Optional.of(exampleBooks.get(1)));
        //Then
        Exception exception = assertThrows( BookAlreadyExistException.class , () -> { underTest.addBooks(toSave); } );
        assertEquals( String.format("Book with ISBN: %s already exist.",isbn), exception.getMessage());
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

        ResponseModel response = underTest.getBooks(author);
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
        //Then
        Exception ex = assertThrows(BookNotExistException.class , () -> { underTest.updateBook(exampleSingleBook,isbn); });
        assertEquals( "Book with provided ISBN does not exist." , ex.getMessage() );
    }

    @Test
    public void tryUpdateBookByIsbnWithInvalidIsbn(){
        //Given
        final String isbn = null;
        exampleSingleBook.setInStorage(55);
        //Then
        Exception ex = assertThrows( InvalidIsbnException.class , () -> { underTest.updateBook(exampleSingleBook,isbn); } );
        assertEquals( "ISBN cannot be null." , ex.getMessage() );
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
        verify(repository).save(newBook);
    }

    @Test
    public void tryUpdateNonExistingBookById(){
        //Given
        final Integer id = exampleSingleBook.getId();
        exampleSingleBook.setInStorage(55);
        //When
        when( repository.findById(id)).thenReturn(Optional.empty());
        //Then
        Exception ex = assertThrows( BookNotExistException.class , () -> { underTest.updateBook(exampleSingleBook,id); } );
        assertEquals("Book with provided id does not exist." , ex.getMessage());
    }

    @Test
    public void tryUpdateBookByIdWithInvalidId(){
        //Given
        final Integer id = null;
        exampleSingleBook.setInStorage(55);
        //Then
        Exception ex = assertThrows( IllegalArgumentException.class , () -> {
            underTest.updateBook(exampleSingleBook,id);
        } );
        assertEquals("Id cannot be null." , ex.getMessage());
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
        verify(repository).save(newBook);
    }

    @Test
    public void isbnValidationTest(){
        //Given
        final String validTenDigitIsbn = "5006009004";
        final String validThirteenDigitIsbn = "6004002006005";
        final String invalidIsbn = "56043";
        final String emptyString = "";
        final String nullIsbn = null;
        //Then
        assertAll(
                "Isbn validation",
                () -> { assertTrue(underTest.isISBNValid(validTenDigitIsbn), "tenDigitIsbn"); },
                () -> { assertTrue( underTest.isISBNValid(validThirteenDigitIsbn) , "thirteenDigitIsbn" );}
        );

        assertAll(
                "Unsuccessful validation tests",
                () -> {
                    Exception ex = assertThrows( InvalidIsbnException.class , () -> { underTest.isISBNValid(invalidIsbn); }  );
                    assertEquals("Invalid ISBN" , ex.getMessage()) ;
                },
                () -> {
                    Exception ex = assertThrows( InvalidIsbnException.class , () -> { underTest.isISBNValid(emptyString); }  );
                    assertEquals("Invalid ISBN" , ex.getMessage());
                },
                () -> {
                    Exception ex = assertThrows( InvalidIsbnException.class , () -> { underTest.isISBNValid(nullIsbn); }  );
                    assertEquals("ISBN cannot be null." , ex.getMessage());
                }
        );
    }

    @Test
    public void isbnExistingInDbTest(){
        //Given
        String existingIsbn = "5006007045";
        String nonExistingIsbn = "6006004005";
        //When
        when(repository.getBookModelByISBN(existingIsbn)).thenReturn(Optional.of(new BookModel()));
        when(repository.getBookModelByISBN(nonExistingIsbn)).thenReturn(Optional.empty());
        //Then
        assertTrue(underTest.doesIsbnExistInDatabase(existingIsbn));
        assertFalse(underTest.doesIsbnExistInDatabase(nonExistingIsbn));
    }

    @Test
    public void prepareBookToUpdateTest(){
        //Given
        BookModel oldBook = exampleSingleBook;
        BookModel newBook =new BookModel();
        newBook.setBookTitle("The Hobbit - Part One");
        newBook.setId(1313);
        newBook.setBookDescription(null);
        newBook.setISBN("5005009004333");
        // When
        BookModel edited = underTest.prepareBookToUpdate(oldBook,newBook);
        //Then
        assertAll(
                "Check properties",
                () -> { assertEquals( 1 , edited.getId() , "Id cannot change" ); },
                () -> { assertEquals( newBook.getBookTitle() , edited.getBookTitle() , "BookTitle should change" ); },
                () -> { assertEquals(oldBook.getBookDescription() , edited.getBookDescription(), "Null shouldnt be accepted"); },
                () -> { assertEquals(oldBook.getISBN() , edited.getISBN() , "ISBN cannot change!"); }
        );

    }

}