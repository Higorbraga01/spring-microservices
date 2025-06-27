package com.example.notificationservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final StringRedisTemplate redisTemplate;

    @RabbitListener(queues = "payment.notification")
    public void receiveNotification(String orderId) {
        String message = "📢 Pedido " + orderId + " pago com sucesso. Notificação armazenada no Redis.";
        redisTemplate.opsForValue().set("notification:" + orderId, message);
        System.out.println(message);
    }
}
