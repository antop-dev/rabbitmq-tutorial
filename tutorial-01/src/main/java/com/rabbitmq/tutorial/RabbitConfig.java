package com.rabbitmq.tutorial;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "hello";

    @Bean
    public Queue hello() {
        return new Queue(QUEUE_NAME);
    }

    @Profile("receiver")
    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

    @Profile("sender")
    @Bean
    public Sender sender(RabbitTemplate template, Queue queue) {
        return new Sender(template, queue);
    }
}
