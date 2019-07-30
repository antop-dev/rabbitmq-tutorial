package com.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "tut.direct";

    @Bean
    public DirectExchange direct() {
        return new DirectExchange(QUEUE_NAME);
    }

    @Profile("receiver")
    static class ReceiverConfig {

        @Bean
        public Queue autoDeleteQueue1() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue autoDeleteQueue2() {
            return new AnonymousQueue();
        }

        // direct exchange → error → receiver1
        @Bean
        public Binding binding1(DirectExchange direct, Queue autoDeleteQueue1) {
            return BindingBuilder.bind(autoDeleteQueue1).to(direct).with("error");
        }

        // direct exchange → error/warn/info/debug → receiver2
        @Bean
        public Binding binding2a(DirectExchange direct, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(direct).with("error");
        }

        @Bean
        public Binding binding2b(DirectExchange direct, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(direct).with("warn");
        }

        @Bean
        public Binding binding2c(DirectExchange direct, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(direct).with("info");
        }

        @Bean
        public Binding binding2d(DirectExchange direct, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(direct).with("debug");
        }

        @Bean
        public Receiver receiver() {
            return new Receiver();
        }
    }

    @Profile("sender")
    @Bean
    public Sender sender(RabbitTemplate rabbitTemplate, DirectExchange exchange) {
        return new Sender(rabbitTemplate, exchange);
    }

}
