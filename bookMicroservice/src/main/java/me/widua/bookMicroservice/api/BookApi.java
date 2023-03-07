package me.widua.bookMicroservice.api;

import me.widua.bookMicroservice.service.BookServiceImpl;
import me.widua.bookMicroservice.models.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class BookApi {
    private final BookServiceImpl manager ;
    @Autowired
    public BookApi (BookServiceImpl manager){
        this.manager = manager;
    }

    @GetMapping("/books")
    public ResponseEntity<?> getBooks(){
        ResponseModel response = manager.getBooks() ;
        if (response.getStatus().equals(HttpStatus.OK)){
            return ResponseEntity.ok(response.getBody());
        }
        if (response.getStatus().equals(HttpStatus.NO_CONTENT)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
