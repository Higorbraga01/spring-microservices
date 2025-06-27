package com.example.paymentservice.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "order.payment")
    public void receiveOrder(String orderId) {
        System.out.println("💰 Processando pagamento para pedido: " + orderId);
        try {
            Thread.sleep(2000); // Simula tempo de processamento
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Envia mensagem para notificação
        rabbitTemplate.convertAndSend("payment.exchange", "payment.notification", orderId);
        System.out.println("✅ Pagamento realizado. Enviando notificação para pedido: " + orderId);
    }
}
