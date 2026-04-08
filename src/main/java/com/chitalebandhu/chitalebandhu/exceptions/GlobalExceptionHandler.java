package com.chitalebandhu.chitalebandhu.exceptions;

import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoTimeoutException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handle(ResourceNotFoundException ex){
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> expired() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Access token expired");
    }

    @ExceptionHandler({MongoTimeoutException.class, MongoSocketOpenException.class})
    public ResponseEntity<String> databaseUnavailable(Exception ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Database is currently unavailable. Please try again in a moment.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> general(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected server error");
    }
}
