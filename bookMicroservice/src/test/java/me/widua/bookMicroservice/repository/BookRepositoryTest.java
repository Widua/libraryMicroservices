package me.widua.bookMicroservice.repository;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.types.BookType;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
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
                BookType.PHYSICAL ,
                15);
        //when
        repository.save(book);
        //then
        assertEquals(repository.findById(1).get(),book);
    }

    @Test
    public void queryingByIsbn(){
        //given
        BookModel book =  new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                "5006001200" ,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        repository.save(book);
        //when
        Optional<BookModel> queriedBook = repository.getBookModelByISBN(book.getISBN());
        //then
        assertTrue(queriedBook.isPresent());
    }

    @Test
    public void queryingByAuthor(){
        //Given
        List<BookModel> books = Arrays.asList(
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
        repository.saveAll(books) ;

        //When
        List<BookModel> queriedBooks = repository.getBookModelsByAuthor("J.K. Rowling").get();

        //Then
        assertEquals(2,queriedBooks.size());
    }

    @Test
    public void queryingByTitle(){
        //Given
        List<BookModel> books = Arrays.asList(
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
                        15)
        );
        repository.saveAll(books);
        //When
        Optional<List<BookModel>> queried = repository.getBookModelByBookTitle("Harry Potter and the Philosopher's Stone");

        assertTrue(queried.isPresent());
        assertEquals(queried.get().size() , 2);
    }

    @Test
    public void updateOneFieldBook(){
        //Given
        BookModel book =  new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                "5006001200" ,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        //When
        repository.save(book);

        BookModel queried = repository.getBookModelByISBN("5006001200").get();

        queried.setInStorage(17);
        repository.save(queried);

        List<BookModel> books = (List<BookModel>) repository.findAll();
        int size = books.size();

        // Then
        assertEquals(repository.findById(queried.getId()).get().getInStorage() , 17 );
        assertEquals(repository.findById(queried.getId()).get() , queried);
        assertEquals(size,1);
    }

    @Test
    public void updateManyFields(){
        //Given
        BookModel book =  new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                "5006001200" ,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        repository.save(book);
        //When
        BookModel queried = repository.getBookModelByISBN(book.getISBN()).get();

        queried.setInStorage(17);
        queried.setBookType(BookType.E_BOOK);
        repository.save(queried);

        List<BookModel> books = (List<BookModel>) repository.findAll();
        int size = books.size();

        // Then
        assertEquals(repository.findById(queried.getId()).get(),book);
        assertEquals(repository.findById(queried.getId()).get().getInStorage() , 17 );
        assertEquals(size,1);
    }

    @Test
    public void delete(){
        //Given
        BookModel book =  new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                "5006001200" ,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        repository.save(book);
        //When

        BookModel queried = repository.getBookModelByISBN(book.getISBN()).get();

        repository.deleteById(queried.getId());
        //Then
        assertTrue(repository.findById(queried.getId()).isEmpty());
    }

}
