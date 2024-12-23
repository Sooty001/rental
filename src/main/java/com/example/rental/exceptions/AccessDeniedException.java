package com.example.rental.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Ошибка доступа!");
    }
}
