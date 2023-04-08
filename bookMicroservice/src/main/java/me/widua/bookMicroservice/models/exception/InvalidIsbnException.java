package me.widua.bookMicroservice.models.exception;

public class InvalidIsbnException extends IllegalArgumentException {
    public InvalidIsbnException(String message){
        super(message);
    }
    public InvalidIsbnException(){
        super();
    }
}
