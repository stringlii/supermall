package com.litianyi.supermall.mq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SupermallMessageQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallMessageQueueApplication.class, args);
    }

}
