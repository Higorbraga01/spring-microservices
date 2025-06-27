package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository repository;
    private RabbitTemplate rabbitTemplate;
    private OrderService service;

    @BeforeEach
    void setup() {
        repository = mock(OrderRepository.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        service = new OrderService(repository, rabbitTemplate);
    }

    @Test
    void shouldCreateOrderAndSendIdToQueue() {
        OrderRequest request = new OrderRequest();
        request.setDescription("Teste");

        Order persisted = new Order();
        persisted.setId(1L);
        persisted.setDescription("Teste");
        persisted.setStatus(OrderStatus.CREATED);

        when(repository.save(any(Order.class))).thenReturn(persisted);

        Order result = service.createOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Teste", result.getDescription());
        assertEquals(OrderStatus.CREATED, result.getStatus());

        verify(repository, times(1)).save(any(Order.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("order.exchange"), eq("order.payment"), eq("1"));
    }

    @Test
    void shouldReturnOrderWhenFound() {
        // arrange
        Order order = new Order();
        order.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(order));

        // act
        Optional<Order> result = service.getOrder(5L);

        // assert
        assertTrue(result.isPresent());
        assertEquals(5L, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // act
        Optional<Order> result = service.getOrder(99L);

        // assert
        assertFalse(result.isPresent());
    }
}
