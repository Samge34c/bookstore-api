package com.taller.bookstore.controller;

import com.taller.bookstore.dto.request.OrderRequest;
import com.taller.bookstore.dto.response.ApiResponse;
import com.taller.bookstore.dto.response.OrderResponse;
import com.taller.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Pedidos", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Crear pedido (usuario autenticado)")
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse order = orderService.create(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Pedido creado exitosamente", 201));
    }

    @GetMapping("/my")
    @Operation(summary = "Consultar mis pedidos")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderResponse> orders = orderService.findMyOrders(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(orders, "Pedidos obtenidos exitosamente"));
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos (solo ADMIN)")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(orderService.findAll(), "Todos los pedidos obtenidos"));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse order = orderService.cancel(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(order, "Pedido cancelado exitosamente"));
    }
}
