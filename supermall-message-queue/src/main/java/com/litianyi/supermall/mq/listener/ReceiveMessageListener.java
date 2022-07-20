package com.litianyi.supermall.mq.listener;

import com.litianyi.supermall.mq.OrderEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = {"hello.java.queue"})
@Slf4j
public class ReceiveMessageListener {

    @RabbitHandler
    public void receiveMessage(Message message, OrderEntity orderEntity, Channel channel) throws IOException {
        log.info("原始内容 {}", message);
        log.info("主体内容 {}", orderEntity);
        log.info("通道 {}", channel);

        // 通道内自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("通道deliveryTag {}", deliveryTag);
        try {
            System.out.println("处理业务逻辑...");
            channel.basicAck(deliveryTag, false);

            // long deliveryTag, boolean multiple（批量拒收）, boolean requeue（是否把消息退回队列）
            // channel.basicNack(deliveryTag, false, true);
            // channel.basicReject(deliveryTag, true);
        } catch (IOException e) {
            // 网络中断
            throw new RuntimeException(e);
        }
    }

}