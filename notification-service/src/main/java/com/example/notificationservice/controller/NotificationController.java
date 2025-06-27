package com.example.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/{orderId}")
    public String getNotification(@PathVariable("orderId") String orderId) {
        return redisTemplate.opsForValue().get("notification:" + orderId);
    }

}
