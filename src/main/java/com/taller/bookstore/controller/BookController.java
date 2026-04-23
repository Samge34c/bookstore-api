package com.taller.bookstore.controller;

import com.taller.bookstore.dto.request.BookRequest;
import com.taller.bookstore.dto.response.ApiResponse;
import com.taller.bookstore.dto.response.BookResponse;
import com.taller.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@Tag(name = "Libros", description = "Catálogo de libros")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Listar libros con paginación y filtros opcionales")
    public ResponseEntity<ApiResponse<Page<BookResponse>>> findAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<BookResponse> result;
        if (categoryId != null) {
            result = bookService.findByCategory(categoryId, pageable);
        } else if (authorId != null) {
            result = bookService.findByAuthor(authorId, pageable);
        } else {
            result = bookService.findAll(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(result, "Libros obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por ID")
    public ResponseEntity<ApiResponse<BookResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bookService.findById(id), "Libro encontrado"));
    }

    @PostMapping
    @Operation(summary = "Crear libro (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<BookResponse>> create(@Valid @RequestBody BookRequest request) {
        BookResponse book = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Libro creado exitosamente", 201));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar libro (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<BookResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookService.update(id, request), "Libro actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar libro (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Libro eliminado exitosamente"));
    }
}
