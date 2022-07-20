package com.litianyi.supermall.mq.controller;

import com.litianyi.supermall.mq.OrderEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author litianyi
 * @version 1.0
 */
@RestController
@RequestMapping("mq")
public class SendMessageController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("send")
    public String sendMessage() {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setCreateTime(new Date());
        orderEntity.setReceiverName("哈哈");
        rabbitTemplate.convertAndSend("hello.java.exchange", "hello.java", orderEntity,
                new CorrelationData(UUID.randomUUID().toString()));
        return "发送成功";
    }

}
