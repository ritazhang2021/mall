package com.rita.modules.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Rita
 * @Date:8/8/2021 8:23 PM
 */
@Configuration
public class MyRedissonConfig {

    //单节点模式，还有云服务模式，分布式模式。。。。

    @Bean
    public RedissonClient redissonClient(){
        //创建配置
        Config config = new Config();
        //如果安全模式要用rediss
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");
        //根据配置，创建出实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
