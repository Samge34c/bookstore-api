package com.taller.bookstore.service;

import com.taller.bookstore.dto.request.OrderRequest;
import com.taller.bookstore.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request, String userEmail);
    List<OrderResponse> findMyOrders(String userEmail);
    List<OrderResponse> findAll();
    OrderResponse cancel(Long orderId, String userEmail);
}
