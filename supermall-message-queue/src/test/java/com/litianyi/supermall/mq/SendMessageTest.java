package com.litianyi.supermall.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author litianyi
 * @version 1.0
 */
@SpringBootTest
public class SendMessageTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setCreateTime(new Date());
        orderEntity.setReceiverName("哈哈");
        rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", orderEntity);
    }

}
