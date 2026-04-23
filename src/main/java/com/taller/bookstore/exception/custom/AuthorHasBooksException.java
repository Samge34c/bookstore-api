package com.taller.bookstore.exception.custom;

public class AuthorHasBooksException extends RuntimeException {
    public AuthorHasBooksException(Long authorId) {
        super("No se puede eliminar el autor con id " + authorId + " porque tiene libros asociados");
    }
}
