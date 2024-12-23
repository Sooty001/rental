package com.example.rental.exceptions;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String message) {
        super("Пользователь с почтой " + message + " уже существует!");
    }
}
