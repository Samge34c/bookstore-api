package com.taller.bookstore.service.impl;

import com.taller.bookstore.dto.request.AuthorRequest;
import com.taller.bookstore.dto.response.AuthorResponse;
import com.taller.bookstore.dto.response.BookResponse;
import com.taller.bookstore.entity.Author;
import com.taller.bookstore.exception.custom.AuthorHasBooksException;
import com.taller.bookstore.exception.custom.ResourceNotFoundException;
import com.taller.bookstore.mapper.AuthorMapper;
import com.taller.bookstore.mapper.BookMapper;
import com.taller.bookstore.repository.AuthorRepository;
import com.taller.bookstore.repository.BookRepository;
import com.taller.bookstore.service.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository,
                             BookRepository bookRepository,
                             AuthorMapper authorMapper,
                             BookMapper bookMapper) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.authorMapper = authorMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public AuthorResponse create(AuthorRequest request) {
        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        return authorMapper.toResponse(
                authorRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Autor", id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor", id));
        author.setName(request.getName());
        author.setBiography(request.getBiography());
        author.setEmail(request.getEmail());
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Autor", id);
        }
        long bookCount = authorRepository.countBooksByAuthorId(id);
        if (bookCount > 0) {
            throw new AuthorHasBooksException(id);
        }
        authorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findBooksByAuthor(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Autor", authorId);
        }
        return bookRepository.findByAuthorId(authorId).stream()
                .map(bookMapper::toResponse)
                .collect(Collectors.toList());
    }
}
