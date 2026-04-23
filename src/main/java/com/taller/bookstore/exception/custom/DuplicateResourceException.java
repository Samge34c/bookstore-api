package com.taller.bookstore.exception.custom;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String resource, String field, String value) {
        super(resource + " con " + field + " '" + value + "' ya existe");
    }
}
