package com.rabbitmq.tutorial;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "tut.topic";

    @Bean
    TopicExchange topic() {
        return new TopicExchange(QUEUE_NAME);
    }

    @Profile("receiver")
    static class ReceiverConfig {

        @Bean
        public Receiver receiver() {
            return new Receiver();
        }

        @Bean
        public Queue autoDeleteQueue1() {
            return new AnonymousQueue();
        }

        @Bean
        public Queue autoDeleteQueue2() {
            return new AnonymousQueue();
        }

        @Bean
        public Binding binding1a(TopicExchange topic, Queue autoDeleteQueue1) {
            return BindingBuilder.bind(autoDeleteQueue1).to(topic).with("*.orange.*");
        }

        @Bean
        public Binding binding1b(TopicExchange topic, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(topic).with("*.*.rabbit");
        }

        @Bean
        public Binding binding2a(TopicExchange topic, Queue autoDeleteQueue2) {
            return BindingBuilder.bind(autoDeleteQueue2).to(topic).with("lazy.#");
        }

    }

    @Profile("sender")
    @Bean
    Sender sender(RabbitTemplate rabbitTemplate, TopicExchange topic) {
        return new Sender(rabbitTemplate, topic);
    }

}
