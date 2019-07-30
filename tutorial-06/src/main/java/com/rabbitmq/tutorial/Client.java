package com.rabbitmq.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class Client {
    private final RabbitTemplate template;
    private final DirectExchange exchange;

    public Client(RabbitTemplate template, DirectExchange exchange) {
        this.template = template;
        this.exchange = exchange;
    }

    private int start = 0;

    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() {
        log.info("[x] Requesting fib({})", start);
        Integer response = (Integer) template.convertSendAndReceive(exchange.getName(), "rpc", start++);
        log.info("[.] Got '{}'", response);
    }

}
