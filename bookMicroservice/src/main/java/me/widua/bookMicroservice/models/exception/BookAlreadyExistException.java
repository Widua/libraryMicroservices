package me.widua.bookMicroservice.models.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BookAlreadyExistException extends RuntimeException {

    public BookAlreadyExistException(String message){
        super(message);
    }

    public BookAlreadyExistException(){
        super();
    }

}
