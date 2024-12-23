package com.example.rental.exceptions;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException() {
        super("Квартира не найдена!");
    }
}
