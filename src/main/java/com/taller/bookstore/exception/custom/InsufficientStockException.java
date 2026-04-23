package com.taller.bookstore.exception.custom;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String bookTitle, int requested, int available) {
        super("Stock insuficiente para '" + bookTitle + "': solicitado=" + requested + ", disponible=" + available);
    }
}
