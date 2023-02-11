package me.widua.bookMicroservice.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
public class ResponseModel {

    private HttpStatus status ;
    private Object body ;

}
