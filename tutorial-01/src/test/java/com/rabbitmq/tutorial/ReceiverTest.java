package com.rabbitmq.tutorial;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.github.javafaker.Faker;
import com.rabbitmq.tutorial.RabbitConfig;
import com.rabbitmq.tutorial.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static com.rabbitmq.tutorial.RabbitConfig.QUEUE_NAME;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("receiver")
public class ReceiverTest {
    private final Faker faker = new Faker();
    @SpyBean
    private Receiver receiver; // target
    @Autowired
    private FakeSender sender; // fake

    @Test
    public void 보낸_메세지와_받은_메세지가_일치한다() throws Exception {
        String message = faker.book().title();
        // action
        sender.send(message);
        // wait received
        Thread.sleep(300);
        // verify
        verify(receiver).receive(eq(message));
    }

    @Test
    public void 보낸_개수와_받은_개수가_일치한다() throws InterruptedException {
        int loop = new Random().nextInt(10) + 1;
        // action
        for (int i = 0; i < loop; i++) {
            sender.send(faker.book().title());
        }
        // wait received
        Thread.sleep(loop * 100);
        // verify
        verify(receiver, times(loop)).receive(anyString());
    }

    @Configuration
    @Import(RabbitConfig.class)
    static class AmqpConfiguration {
        @Bean
        ConnectionFactory connectionFactory() {
            return new CachingConnectionFactory(new MockConnectionFactory());
        }

        @Bean
        RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }

        @Bean
        RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            return new RabbitTemplate(connectionFactory);
        }

    }

    @Configuration
    @Import(AmqpConfiguration.class)
    static class AmqpProducerConfiguration {

        @Bean
        SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(QUEUE_NAME);
            container.setMessageListener(listenerAdapter);
            return container;
        }

        @Bean
        MessageListenerAdapter listenerAdapter(Receiver receiver) {
            return new MessageListenerAdapter(receiver, "receive");
        }

        @Bean
        FakeSender sender(RabbitTemplate rabbitTemplate, Queue queue) {
            return new FakeSender(rabbitTemplate, queue);
        }
    }

    /**
     * faker sender
     */
    @Slf4j
    static class FakeSender {
        private final RabbitTemplate template;
        private final Queue queue;

        public FakeSender(RabbitTemplate template, Queue queue) {
            this.template = template;
            this.queue = queue;
        }

        public void send(String message) {
            template.convertAndSend(queue.getName(), message);
            log.info("[x] Sent '{}'", message);
        }
    }

}
