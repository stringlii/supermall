package com.litianyi.supermall.product;

import com.litianyi.common.utils.SnowflakeIdWorker;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
class SupermallProductApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //save
        ops.set("hello", "world_" + SnowflakeIdWorker.getStrId());
        //get
        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    void redissonTest(){
        System.out.println(redissonClient);
    }
}
