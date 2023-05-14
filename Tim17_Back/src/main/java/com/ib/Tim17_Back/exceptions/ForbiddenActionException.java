package com.ib.Tim17_Back.exceptions;

public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException() {
        super("User is not allowed to perform this action");
    }
}
