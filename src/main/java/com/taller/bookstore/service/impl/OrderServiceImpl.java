package com.taller.bookstore.service.impl;

import com.taller.bookstore.dto.request.OrderRequest;
import com.taller.bookstore.dto.response.OrderResponse;
import com.taller.bookstore.entity.*;
import com.taller.bookstore.exception.custom.*;
import com.taller.bookstore.mapper.OrderMapper;
import com.taller.bookstore.repository.BookRepository;
import com.taller.bookstore.repository.OrderRepository;
import com.taller.bookstore.repository.UserRepository;
import com.taller.bookstore.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            BookRepository bookRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponse create(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email " + userEmail + " no encontrado"));

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.getItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Libro", itemRequest.getBookId()));

            if (book.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(book.getTitle(), itemRequest.getQuantity(), book.getStock());
            }

            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .book(book)
                    .quantity(itemRequest.getQuantity())
                    .subtotal(subtotal)
                    .build();
            items.add(item);
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(total)
                .build();

        // Asignar la referencia al pedido en cada ítem y descontar stock
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            item.setOrder(order);

            Book book = bookRepository.findById(request.getItems().get(i).getBookId()).get();
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);
        }

        order.setItems(items);
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email " + userEmail + " no encontrado"));

        return orderRepository.findByUserId(user.getId()).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse cancel(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", orderId));

        // Verificar que el pedido pertenece al usuario (o es admin — se valida en controller)
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new UnauthorizedAccessException("No tiene permisos para cancelar este pedido");
        }

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException("No se puede cancelar un pedido con estado CONFIRMED");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("El pedido ya se encuentra cancelado");
        }

        // Restaurar stock
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setStock(book.getStock() + item.getQuantity());
            bookRepository.save(book);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toResponse(orderRepository.save(order));
    }
}
