package com.taller.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    private String description;
}
