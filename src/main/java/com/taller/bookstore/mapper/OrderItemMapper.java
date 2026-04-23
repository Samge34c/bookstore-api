package com.taller.bookstore.mapper;

import com.taller.bookstore.dto.response.OrderItemResponse;
import com.taller.bookstore.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBook().getId())
                .bookTitle(item.getBook().getTitle())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
