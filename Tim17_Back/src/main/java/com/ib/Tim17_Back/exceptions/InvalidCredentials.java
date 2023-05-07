package com.ib.Tim17_Back.exceptions;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials() {
        super("Invalid credentials!");
    }
}
