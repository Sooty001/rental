package com.example.rental.exceptions;

public class AgreementNotFoundException extends RuntimeException {
    public AgreementNotFoundException() {
        super("Договор не найден!");
    }
    public AgreementNotFoundException(String massage) {
        super(massage);
    }
}
