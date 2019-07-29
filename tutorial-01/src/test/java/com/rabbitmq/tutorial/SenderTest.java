package com.rabbitmq.tutorial;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.rabbitmq.tutorial.RabbitConfig;
import com.rabbitmq.tutorial.Sender;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.rabbitmq.tutorial.RabbitConfig.QUEUE_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("sender")
public class SenderTest {
    @Autowired
    private Sender sender; // target
    @SpyBean
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private FakeReceiver receiver; // faker

    @After
    public void tearDown() {
        receiver.getMessages().clear();
    }

    @Test
    public void 보낸_개수와_받은_개수가_일치한다() throws InterruptedException {
        int loop = new Random().nextInt(10) + 1;
        // action
        for (int i = 0 ; i < loop ; i++) {
            sender.send();
        }
        // wait received
        Thread.sleep(loop * 100);
        // verify
        verify(rabbitTemplate, times(loop)).convertAndSend(eq(QUEUE_NAME), anyString());
        assertEquals(loop, receiver.getMessages().size());
    }

    @Test
    public void 보낸_메세지와_받은_메세지가_일치한다() throws Exception {
        // action
        sender.send();
        // wait received
        Thread.sleep(500);
        // verify
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate).convertAndSend(eq(QUEUE_NAME), captor.capture());
        assertEquals(captor.getValue(), receiver.getMessages().get(0));
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
    static class AmqpConsumerConfiguration {
        @Bean
        SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(QUEUE_NAME);
            container.setMessageListener(receiver());
            return container;
        }

        @Bean
        FakeReceiver receiver() {
            return new FakeReceiver();
        }
    }

    /**
     * faker receiver
     */
    @Slf4j
    static class FakeReceiver implements MessageListener {
        private final List<String> messages = new ArrayList<>();

        List<String> getMessages() {
            return messages;
        }

        @Override
        public void onMessage(Message message) {
            String body = new String(message.getBody());
            log.info("[x] Received '{}'", body);
            messages.add(body);
        }
    }

}
