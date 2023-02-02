package me.widua.bookMicroservice.manager;

import me.widua.bookMicroservice.BookMicroserviceApplication;
import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.types.BookType;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BookMicroserviceApplication.class
)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
class BookManagerTest {
    @Autowired
    private BookManager underTest ;
    @Autowired
    private BookRepository repository ;


    @BeforeEach
    void setUp() {
        // Initial Values
        List<BookModel> books = Arrays.asList(
                new BookModel("J.K. Rowling", "Harry Potter and the Philosopher's Stone" , "5006001200" , "First book of Harry Potter adventures" , BookType.PHYSICAL , 15),
                new BookModel("J.K. Rowling", "Harry Potter and the Prisoner of Azkaban" , "2005006320" , "Another book of Harry Potter adventures" , BookType.PHYSICAL , 12),
                new BookModel("William Shakespeare", "Makhbet" , "900232559" , "One of the most popular book from W. Shakespeare" , BookType.PHYSICAL , 3)
        );
        // Put initial values to database
        repository.saveAll(books);
    }

    @AfterEach
     void tearDown() {
        // Clear database
        repository.deleteAll();
    }

    @Test
    public void doesSingleBookAdds(){
        final String ISBN = "900900900" ;
       // Given
       BookModel toInsertInDB = new BookModel("Bolesław Prus",
               "Lalka",
               ISBN,
               "Popular polish book",
               BookType.PHYSICAL,
               4);
       // When
        underTest.addBook(toInsertInDB);
        Optional<BookModel> bookOptional = repository.getBookModelByISBN(ISBN);
        Iterable<BookModel> books = repository.findAll();
       //Then
        assertTrue(bookOptional.isPresent());

    }

    @Test
    public void doesAddingInvalidISBNWorks(){
        //Given
        BookModel wrongIsbnBook = new BookModel("William Shakespeare", "Makhbet" , "900232559" , "One of the most popular book from W. Shakespeare" , BookType.PHYSICAL , 3);
        //When
        ResponseEntity<?> response = underTest.addBook(wrongIsbnBook) ;
        //Then
        assertEquals(response.getStatusCode() , HttpStatus.BAD_REQUEST);
    }

    @Test
    public void doesAddingInvalidISBNInMultipleBooksWorks(){
        //Given
        List<BookModel> newBooks = Arrays.asList(
                new BookModel("J.K. Rowling", "Harry Potter and the Philosopher's Stone" , "9002001200" , "First book of Harry Potter adventures" , BookType.E_BOOK , 15),
                new BookModel("J.K. Rowling", "Harry Potter and the Philosopher's Stone" , "1001001002" , "First book of Harry Potter adventures" , BookType.AUDIOBOOK , 15),
                new BookModel("J.K. Rowling", "Harry Potter and the Philosopher's Stone" , "5006001200" , "First book of Harry Potter adventures" , BookType.PHYSICAL , 15)
        ) ;
        //When
        ResponseEntity<?> response = underTest.addBooks(newBooks) ;
        String wannabeBody = "Adding stopped, because of bad ISBN in 2 index";
        String body = (String) response.getBody();
        //Then
        assertEquals(wannabeBody,body);
    }

}