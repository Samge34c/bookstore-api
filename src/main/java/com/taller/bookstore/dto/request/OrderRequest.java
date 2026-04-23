package com.taller.bookstore.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotEmpty(message = "El pedido debe contener al menos un ítem")
    @Valid
    private List<OrderItemRequest> items;
}
