package com.example.rental.exceptions;

public class PhoneAlreadyInUseException extends RuntimeException {
    public PhoneAlreadyInUseException(String message) {
        super("Пользователь с номером " + message + " уже существует!");
    }
}
