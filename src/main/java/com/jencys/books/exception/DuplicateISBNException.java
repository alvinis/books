package com.jencys.books.exception;

public class DuplicateISBNException extends RuntimeException {
    public DuplicateISBNException(String message) {
        super(message);
    }
}