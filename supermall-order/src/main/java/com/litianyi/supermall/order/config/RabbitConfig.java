package com.litianyi.supermall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author litianyi
 * @version 1.0
 */
@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 使用 JSON 序列化转换消息
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制 RabbitMQTemplate
     */
    @PostConstruct
    public void initRabbitTemplate() {
        // 消息抵达 broke 回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * @param correlationData 当前消息的唯一关联数据（消息的唯一id）
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    log.error("publishConfirm 消息发送到交换器被退回，Id：{}；退回原因是：{}", correlationData.getId(), cause);
                } else {
                    log.info("发送消息到交换器成功，MessageId：{}", correlationData.getId());
                }
            }
        });

        // 消息抵达 queue 回调（消息没有投递给指定的队列才会触发这个失败回调）
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * @param message    投递失败的消息
             * @param replyCode  回复的状态码
             * @param replyText  回复的文本内容
             * @param exchange   这个消息发给哪个交换机
             * @param routingKey 这个消息用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息 {} 被退回，退回状态码 {}，退回原因 {}，交换机 {},路由键 {}",
                        message, replyCode, replyText, exchange, routingKey);
            }
        });
    }

}
