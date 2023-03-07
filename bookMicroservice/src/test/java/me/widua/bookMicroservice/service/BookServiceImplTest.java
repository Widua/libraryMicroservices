package me.widua.bookMicroservice.service;

import me.widua.bookMicroservice.BookMicroserviceApplication;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
class BookServiceImplTest {
    private BookService underTest ;
    @Mock
    private BookRepository repository ;


    @BeforeEach
    public void setUp(){
        underTest = new BookServiceImpl(repository);
    }

    @Test
    public void canGetAllBooks(){
        //When
        underTest.getBooks();
        //Ten
        verify(repository).findAll();
    }



}