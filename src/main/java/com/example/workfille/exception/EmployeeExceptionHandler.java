package com.example.workfille.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmployeeExceptionHandler {
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EmployeeNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Сотрудник с id= %d не найден".formatted(e.getId()));

    }
    @ExceptionHandler(EmployeeNotValidException.class)
    public ResponseEntity<?>handleBadRequest(EmployeeNotValidException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Не корректные парметры сотрудника: %".formatted(e.getEmployee()));
    }
}
