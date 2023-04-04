package com.ib.Tim17_Back.exceptions;

public class StatusNotPendingException extends RuntimeException {
    public StatusNotPendingException() {
        super("Request already processed!");
    }
}
