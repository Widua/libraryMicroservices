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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

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


    @BeforeEach
    public void setUp(){
        underTest = new BookManager(repository);
    }

    @Test
    public void canGetAllBooks(){
        //When
        underTest.getBooks();
        //Ten
        verify(repository).findAll();
    }



}