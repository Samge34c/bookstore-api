package com.taller.bookstore.exception.custom;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " con id " + id + " no fue encontrado");
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
