package com.rabbitmq.tutorial;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME = "tut.rpc";

    @Profile("client")
    static class ClientConfig {
        @Bean
        DirectExchange exchange() {
            return new DirectExchange(EXCHANGE_NAME);
        }

        @Bean
        Client client(RabbitTemplate rabbitTemplate, DirectExchange exchange) {
            return new Client(rabbitTemplate, exchange);
        }
    }

    @Profile("server")
    static class ServerConfig {
        @Bean
        Queue queue() {
            return new Queue("tut.rpc.requests");
        }

        @Bean
        DirectExchange exchange() {
            return new DirectExchange(EXCHANGE_NAME);
        }

        @Bean
        Binding binding(DirectExchange exchange, Queue queue) {
            return BindingBuilder.bind(queue).to(exchange).with("rpc");
        }

        @Bean
        Server server() {
            return new Server();
        }
    }

}
