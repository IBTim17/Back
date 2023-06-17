package com.ib.Tim17_Back.exceptions;

public class InvalidRecaptchaException extends RuntimeException {
    public InvalidRecaptchaException(String message) {
        super(message);
    }
}
