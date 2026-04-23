package com.taller.bookstore.service;

import com.taller.bookstore.dto.request.AuthorRequest;
import com.taller.bookstore.dto.response.AuthorResponse;
import com.taller.bookstore.dto.response.BookResponse;

import java.util.List;

public interface AuthorService {
    AuthorResponse create(AuthorRequest request);
    AuthorResponse findById(Long id);
    List<AuthorResponse> findAll();
    AuthorResponse update(Long id, AuthorRequest request);
    void delete(Long id);
    List<BookResponse> findBooksByAuthor(Long authorId);
}
