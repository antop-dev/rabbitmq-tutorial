package com.rabbitmq.tutorial;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class Sender {
    private final RabbitTemplate template;
    private final Queue queue;
    private final Faker faker;

    public Sender(RabbitTemplate template, Queue queue) {
        this.template = template;
        this.queue = queue;
        faker = new Faker();
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 1000)
    public void send() {
        String message = "Hello " + faker.name().fullName();
        template.convertAndSend(queue.getName(), message);
        log.info("[x] Sent '{}'", message);
    }

}
