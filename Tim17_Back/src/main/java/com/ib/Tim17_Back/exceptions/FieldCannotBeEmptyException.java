package com.ib.Tim17_Back.exceptions;

public class FieldCannotBeEmptyException extends RuntimeException{
    public FieldCannotBeEmptyException() {
        super("Field cannot be empty!");
    }
}
