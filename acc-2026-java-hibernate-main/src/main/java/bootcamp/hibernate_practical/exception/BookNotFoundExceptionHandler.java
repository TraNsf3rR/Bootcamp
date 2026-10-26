package bootcamp.hibernate_practical.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookNotFoundExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Void> handleBookNotFound(BookNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
