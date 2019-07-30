package com.rabbitmq.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Sender {
    private final RabbitTemplate template;
    private final FanoutExchange fanout;

    public Sender(RabbitTemplate template, FanoutExchange fanout) {
        this.template = template;
        this.fanout = fanout;
    }

    private final AtomicInteger dots = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        StringBuilder builder = new StringBuilder("Hello");
        if (dots.getAndIncrement() == 3) {
            dots.set(1);
        }
        for (int i = 0; i < dots.get(); i++) {
            builder.append('.');
        }
        builder.append(count.incrementAndGet());
        String message = builder.toString();
        template.convertAndSend(fanout.getName(), "", message);
        log.info("[x] Sent '{}'", message);
    }

}
