package com.rabbitmq.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Sender {
    private final RabbitTemplate template;
    private final TopicExchange topic;

    public Sender(RabbitTemplate template, TopicExchange topic) {
        this.template = template;
        this.topic = topic;
    }

    private final AtomicInteger index = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);

    private final String[] keys = {"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox",
            "lazy.brown.fox", "lazy.pink.rabbit", "quick.brown.fox"};

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        StringBuilder builder = new StringBuilder("Hello to ");
        if (this.index.incrementAndGet() == keys.length) {
            this.index.set(0);
        }
        String key = keys[this.index.get()];
        builder.append(key).append(' ');
        builder.append(this.count.incrementAndGet());
        String message = builder.toString();
        template.convertAndSend(topic.getName(), key, message);
        log.info("[x] Sent '{}'", message);
    }

}
