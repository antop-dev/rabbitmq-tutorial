package com.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
public class Receiver {

    @RabbitListener(queues = "#{autoDeleteQueue1.name}")
    public void receive1(String in) {
        receive(in, 1);
    }

    @RabbitListener(queues = "#{autoDeleteQueue2.name}")
    public void receive2(String in) {
        receive(in, 2);
    }

    public void receive(String in, int receiver) {
        log.info("instance {} [x] Received '{}'", receiver, in);
    }

}
