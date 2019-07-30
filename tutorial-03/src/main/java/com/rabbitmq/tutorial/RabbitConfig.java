package com.rabbitmq.tutorial;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "tut.fanout";

    @Bean
    FanoutExchange fanout() {
        return new FanoutExchange(EXCHANGE_NAME);
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

        @Bean
        public Binding binding1(FanoutExchange fanout, Queue autoDeleteQueue1) {
            return BindingBuilder.bind(autoDeleteQueue1).to(fanout);
        }

        @Bean
        public Binding binding2(FanoutExchange fanout, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(fanout);
        }

        @Bean
        public Receiver receiver() {
            return new Receiver();
        }
    }

    @Profile("sender")
    @Bean
    Sender sender(RabbitTemplate rabbitTemplate, FanoutExchange fanout) {
        return new Sender(rabbitTemplate, fanout);
    }

}
