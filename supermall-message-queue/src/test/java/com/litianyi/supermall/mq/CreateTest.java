package com.litianyi.supermall.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 使用 AmqpAdmin 进行创建
 *
 * @author litianyi
 * @version 1.0
 */
@SpringBootTest
public class CreateTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Test
    public void createExchange() {
        DirectExchange directExchange = new DirectExchange("hello.java.exchange");
        amqpAdmin.declareExchange(directExchange);
    }

    @Test
    public void createQueue() {
        amqpAdmin.declareQueue(new Queue("hello.java.queue"));
    }

    @Test
    public void createBinding() {
        amqpAdmin.declareBinding(new Binding("hello.java.queue",
                Binding.DestinationType.QUEUE,
                "hello.java.exchange",
                "hello.java",
                null));
    }

}
