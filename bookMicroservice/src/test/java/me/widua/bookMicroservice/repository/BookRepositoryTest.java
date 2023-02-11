package me.widua.bookMicroservice.repository;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.types.BookType;
import me.widua.bookMicroservice.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookRepositoryTest {

    @Autowired
    private BookRepository repository ;

    @Test
    public void addingBook(){
        BookModel book =  new BookModel(
                "J.K. Rowling",
                "Harry Potter and the Philosopher's Stone" ,
                "5006001200" ,
                "First book of Harry Potter adventures" ,
                BookType.PHYSICAL ,
                15);
        repository.save(book);

        assertEquals(repository.findById(1).get(),book);
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

        book.setInStorage(17);
        book.setId(1);
        repository.save(book);

        List<BookModel> books = (List<BookModel>) repository.findAll();
        int size = books.size();

        // Then
        assertEquals(repository.findById(1).get(),book);
        assertEquals(repository.findById(1).get().getInStorage() , 17 );
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
        //When
        repository.save(book);

        book.setInStorage(17);
        book.setBookType(BookType.E_BOOK);
        book.setId(1);
        repository.save(book);

        List<BookModel> books = (List<BookModel>) repository.findAll();
        int size = books.size();

        // Then
        assertEquals(repository.findById(1).get(),book);
        assertEquals(repository.findById(1).get().getInStorage() , 17 );
        assertEquals(size,1);
    }


}
