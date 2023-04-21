package me.widua.bookMicroservice.models.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BookNotExistException extends RuntimeException{
    public BookNotExistException(){}
    public BookNotExistException(String message){
        super(message);
    }
}
