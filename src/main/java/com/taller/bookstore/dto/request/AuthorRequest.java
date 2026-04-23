package com.taller.bookstore.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    private String biography;

    @Email(message = "Formato de email inválido")
    private String email;
}
