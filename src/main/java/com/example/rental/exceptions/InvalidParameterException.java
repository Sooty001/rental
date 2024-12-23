package com.example.rental.exceptions;

public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String message) {
        super("Ввод " + message + " некорректен, попробуйте еще раз");
    }
}