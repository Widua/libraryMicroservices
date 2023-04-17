package me.widua.bookMicroservice.models.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidIsbnException extends IllegalArgumentException {
    public InvalidIsbnException(String message){
        super(message);
    }
    public InvalidIsbnException(){
        super();
    }
}
