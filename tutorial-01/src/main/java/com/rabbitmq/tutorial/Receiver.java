package com.rabbitmq.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
@RabbitListener(queues = RabbitConfig.QUEUE_NAME)
public class Receiver {

    @RabbitHandler
    public void receive(String in) {
        log.info("[x] Received '{}'", in);
    }

}
