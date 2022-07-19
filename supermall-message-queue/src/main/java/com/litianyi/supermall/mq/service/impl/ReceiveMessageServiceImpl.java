package com.litianyi.supermall.mq.service.impl;

import com.litianyi.supermall.mq.OrderEntity;
import com.litianyi.supermall.mq.service.ReceiveMessageService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author litianyi
 * @version 1.0
 */
@Service("receiveMessageService")
public class ReceiveMessageServiceImpl implements ReceiveMessageService {

    @RabbitListener(queues = {"hello.java.queue"})
    public void receiveMessage(Message message, OrderEntity orderEntity, Channel channel) {
        System.out.println("原始内容：" + message);
        System.out.println("body：" + orderEntity);
        System.out.println("通道：" + channel);
    }

}
