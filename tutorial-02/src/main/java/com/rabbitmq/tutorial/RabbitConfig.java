package com.rabbitmq.tutorial;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "work";

    @Bean
    public Queue hello() {
        return new Queue(QUEUE_NAME, true);
    }

    @Profile("receiver")
    private static class ReceiverConfig {

        @Bean
        public Receiver receiver1() {
            return new Receiver(1);
        }

        @Bean
        public Receiver receiver2() {
            return new Receiver(2);
        }

    }

    @Profile("sender")
    @Bean
    public Sender sender(RabbitTemplate rabbitTemplate, Queue queue) {
        return new Sender(rabbitTemplate, queue);
    }

}
