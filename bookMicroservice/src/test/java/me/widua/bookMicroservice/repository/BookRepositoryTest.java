package me.widua.bookMicroservice.repository;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.types.BookType;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest()
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookRepositoryTest {

    @Autowired
    private BookRepository repository ;

    @BeforeEach
    public void setDatabase(){
        List<BookModel> books = Arrays.asList(
                new BookModel(
                        "J.K. Rowling",
                        "Harry Potter and the Philosopher's Stone" ,
                        "70080045670" ,
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
        repository.saveAll(books);
    }

    @AfterEach
    public void clearDb(){
        repository.deleteAll();
    }

    @Test
    public void addingBook(){
        //given
        BookModel book =  new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                "5006001200" ,
                "First book of Harry Potter adventures" ,
                BookType.AUDIOBOOK ,
                15);
        //when
        repository.save(book);
        //then
        assertTrue(repository.getBookModelByISBN("5006001200").isPresent());
    }

    @Test
    public void queryingByIsbn(){
        //given
        String isbn = "70080045670";
        //when
        Optional<BookModel> queriedBook = repository.getBookModelByISBN(isbn);
        //then
        assertTrue(queriedBook.isPresent());
    }

    @Test
    public void queryingByAuthor(){
        //Given
        String author = "J.K. Rowling";
        //When
        List<BookModel> queriedBooks = repository.getBookModelsByAuthor(author).get();
        //Then
        assertEquals(2,queriedBooks.size());
    }

    @Test
    public void queryingByTitle(){
        //Given
        String title = "Harry Potter and the Philosopher's Stone";
        //When
        Optional<List<BookModel>> queried = repository.getBookModelByBookTitle(title);
        //Then
        assertTrue(queried.isPresent());
        assertEquals(queried.get().size() , 2);
    }

    @Test
    public void updateOneFieldBook(){
        //Given
        BookModel queried = repository.getBookModelByISBN("9009008500").get();

        //When
        queried.setInStorage(17);
        repository.save(queried);

        List<BookModel> danteBooks = repository.getBookModelsByAuthor("Dante Alighieri").get();

        // Then
        assertEquals(repository.findById(queried.getId()).get().getInStorage() , 17 );
        assertEquals(repository.findById(queried.getId()).get() , queried);
        assertEquals(danteBooks.size(),1);
    }

    @Test
    public void updateManyFields(){
        //Given
        BookModel queried = repository.getBookModelByISBN("9009008500").get();
        Integer id = queried.getId();
        //When
        queried.setInStorage(17);
        queried.setBookType(BookType.AUDIOBOOK);
        repository.save(queried);

        List<BookModel> danteBooks = repository.getBookModelsByAuthor("Dante Alighieri").get();
        BookModel updatedBook = repository.findById(id).get();
        // Then
        assertEquals(updatedBook.getInStorage() , 17 );
        assertEquals(updatedBook.getBookType() , BookType.AUDIOBOOK);
        assertEquals(danteBooks.size(),1);
    }

    @Test
    public void delete(){
        //Given
        Integer id = repository.getBookModelByISBN("9009008500").get().getId();
        //When
        repository.deleteById(id);
        //Then
        assertTrue(repository.findById(id).isEmpty());
    }

}
