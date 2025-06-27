package com.example.orderservice;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void createOrder_shouldPersistAndSendMessage() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setDescription("Pedido de teste");

        // Act
        Order saved = orderService.createOrder(request);

        // Assert - banco
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orderRepository.findById(saved.getId())).isPresent();

        // Assert - RabbitMQ
        verify(rabbitTemplate).convertAndSend("order.exchange", "order.payment", saved.getId().toString());
    }

    @Test
    void getOrder_shouldReturnOrderById() {
        // Arrange
        Order order = new Order();
        order.setDescription("Consulta");
        order.setStatus(OrderStatus.CREATED);
        Order saved = orderRepository.save(order);

        // Act
        Order found = orderService.getOrder(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getDescription()).isEqualTo("Consulta");
    }
}
