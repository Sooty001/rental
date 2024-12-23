package com.example.rental.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Ничего не найдено!");
    }
}
