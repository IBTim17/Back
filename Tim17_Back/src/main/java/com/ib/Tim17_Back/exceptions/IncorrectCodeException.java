package com.ib.Tim17_Back.exceptions;

public class IncorrectCodeException extends RuntimeException {
    public IncorrectCodeException() {
        super("Code is expired or not correct!");
    }
}
