package com.example.rental.exceptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public String emailAlreadyInUseException(EmailAlreadyInUseException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(PhoneAlreadyInUseException.class)
    public String phoneAlreadyInUseException(PhoneAlreadyInUseException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public String propertyNotFoundException(PropertyNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(AgreementNotFoundException.class)
    public String agreementNotFoundException(AgreementNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public String reviewAlreadyExistsException(ReviewAlreadyExistsException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(NotFoundException.class)
    public String notFoundException(NotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(PasswordConfirmationMismatchException.class)
    public String passwordConfirmationMismatchException(PasswordConfirmationMismatchException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(InvalidParameterException.class)
    public String invalidParameterException(InvalidParameterException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }

    @ExceptionHandler(ValidateDateRangeException.class)
    public String validateDateRangeException(ValidateDateRangeException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "exception";
    }
}
