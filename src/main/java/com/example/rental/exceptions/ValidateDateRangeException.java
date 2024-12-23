package com.example.rental.exceptions;

public class ValidateDateRangeException extends RuntimeException {
    public ValidateDateRangeException() {
        super("Некорректный диапазон дат!");
    }
}
