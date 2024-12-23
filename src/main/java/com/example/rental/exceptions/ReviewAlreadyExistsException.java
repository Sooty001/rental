package com.example.rental.exceptions;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException() {
        super("Отзыв по этому договору уже существует!");
    }
}
