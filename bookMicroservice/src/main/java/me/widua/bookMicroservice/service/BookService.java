package me.widua.bookMicroservice.service;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    ResponseModel getBooks();
    ResponseModel getBooks(String author);
    ResponseModel getBook(Integer id);
    ResponseModel getBook(String isbn);
    ResponseModel addBook(BookModel book);
    ResponseModel addBooks(Iterable<BookModel> books);
    ResponseModel updateBook(BookModel newBook, String isbn, boolean nullAcceptance);
    ResponseModel updateBook(BookModel newBook, Integer id, boolean nullAcceptance);
}

