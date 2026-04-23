package com.taller.bookstore.controller;

import com.taller.bookstore.dto.request.AuthorRequest;
import com.taller.bookstore.dto.response.ApiResponse;
import com.taller.bookstore.dto.response.AuthorResponse;
import com.taller.bookstore.dto.response.BookResponse;
import com.taller.bookstore.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@Tag(name = "Autores", description = "Gestión de autores")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los autores")
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(authorService.findAll(), "Autores obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener autor por ID")
    public ResponseEntity<ApiResponse<AuthorResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorService.findById(id), "Autor encontrado"));
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Listar libros de un autor")
    public ResponseEntity<ApiResponse<List<BookResponse>>> findBooks(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorService.findBooksByAuthor(id), "Libros del autor obtenidos"));
    }

    @PostMapping
    @Operation(summary = "Crear autor (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<AuthorResponse>> create(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse author = authorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(author, "Autor creado exitosamente", 201));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar autor (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<AuthorResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authorService.update(id, request), "Autor actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar autor (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Autor eliminado exitosamente"));
    }
}
