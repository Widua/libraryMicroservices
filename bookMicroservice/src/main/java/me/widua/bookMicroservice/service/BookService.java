package me.widua.bookMicroservice.service;

import me.widua.bookMicroservice.models.BookModel;
import me.widua.bookMicroservice.models.ResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {

    ResponseModel getBooks();
    ResponseModel getBooks(String author);
    ResponseModel getBook(Integer id);
    ResponseModel getBook(String isbn);
    ResponseModel addBook(BookModel book);
    ResponseModel addBooks(List<BookModel> books);
    ResponseModel updateBook(BookModel newBook, String isbn);
    ResponseModel updateBook(BookModel newBook, Integer id);
}

