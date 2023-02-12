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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @MockBean
    private BookRepository repository ;

    private List<BookModel> booksDb ;

    @BeforeEach
    public void setUp(){
        booksDb =  Arrays.asList(
                new BookModel(
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "5006001200" ,
                        "First book of Harry Potter adventures" ,
                        BookType.PHYSICAL ,
                        15),
                new BookModel(
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "1001002003" ,
                        "First book of Harry Potter adventures" ,
                        BookType.E_BOOK ,
                        15),
                new BookModel(
                        "Dante Alighieri",
                        "Divine comedy" ,
                        "9009008500" ,
                        "Classic of literature" ,
                        BookType.PHYSICAL ,
                        15)
        );


    }


    @Test
    public void getAllBooks(){
        //Given
        Mockito.when(repository.findAll()).thenReturn(booksDb);
        //When
        ResponseModel response = underTest.getBooks();
        //Then
        assertEquals(response.getStatus(),HttpStatus.OK);
        assertTrue(response.getBody() instanceof List);

        List<BookModel> books = (List<BookModel>) response.getBody();
        assertEquals(books.size() , 3);


    }


}