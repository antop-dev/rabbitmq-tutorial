package com.rabbitmq.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Sender {
    private final RabbitTemplate template;
    private final Queue queue;

    private final AtomicInteger dots = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);

    public Sender(RabbitTemplate template, Queue queue) {
        this.template = template;
        this.queue = queue;
    }

    @PostConstruct
    public void send() {
        // 6.5s job
        send(count.incrementAndGet(), 6500);
        // 1s job * 9
        for (int i = 0; i < 9; i++) {
            send(count.incrementAndGet(), 1000);
        }
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 1000)
    public void sendScheduled() {
        if (dots.incrementAndGet() == 10) {
            dots.set(1);
        }
        send(count.incrementAndGet(), dots.get() * 1000);
    }

    private void send(int n, int ms) {
        StringBuilder builder = new StringBuilder("Hello #");
        builder.append(n);
        builder.append(" ");

        int sec = ms / 1000;
        for (int i = 0; i < sec; i++) {
            builder.append('.');
        }
        if (ms % 1000 > 0) { // 나머지는 500ms로 한다.
            builder.append(',');
        }
        send(builder.toString());
    }

    private void send(String message) {
        template.convertAndSend(queue.getName(), message);
        // "." : 1000 ms
        // "," : 500 ms
        log.info("[x] Sent '{}'", message);
    }

}
