package com.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Sender {
    private final RabbitTemplate template;
    private final DirectExchange direct;

    public Sender(RabbitTemplate template, DirectExchange direct) {
        this.template = template;
        this.direct = direct;
    }

    private final AtomicInteger index = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);
    private final String[] keys = {"debug", "info", "warn", "error"};

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        StringBuilder builder = new StringBuilder("Hello to ");
        String key = keys[index.get()];
        builder.append(key).append(' ');
        builder.append(count.incrementAndGet());

        String message = builder.toString();
        template.convertAndSend(direct.getName(), key, message);
        log.info("[x] Sent '{}'", message);

        if (index.incrementAndGet() == keys.length) {
            index.set(0);
        }
    }

}