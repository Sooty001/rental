package com.example.rental.exceptions;

public class PasswordConfirmationMismatchException extends RuntimeException {
    public PasswordConfirmationMismatchException() {
        super("Пароли не совпадают. Пожалуйста, повторите ввод!");
    }
}
