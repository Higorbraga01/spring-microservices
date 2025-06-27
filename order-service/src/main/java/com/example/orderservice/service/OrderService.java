package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setDescription(request.getDescription());
        order.setStatus(OrderStatus.CREATED);

        Order saved = repository.save(order);
        rabbitTemplate.convertAndSend("order.exchange", "order.payment", saved.getId().toString());
        return saved;
    }

    public Optional<Order> getOrder(Long id) {
        return repository.findById(id);
    }
}
