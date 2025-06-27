package com.example.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue orderPaymentQueue() {
        return new Queue("order.payment");
    }

    @Bean
    public Queue paymentNotificationQueue() {
        return new Queue("payment.notification");
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange("payment.exchange");
    }

    @Bean
    public Binding bindingNotification(
            @Qualifier("paymentNotificationQueue") Queue paymentNotificationQueue,
            DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentNotificationQueue).to(paymentExchange).with("payment.notification");
    }
}
