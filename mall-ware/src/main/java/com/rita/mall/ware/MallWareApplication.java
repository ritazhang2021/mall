package com.rita.mall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//@EnableTransactionManagement 移到config包下的java文件中
//@MapperScan("com.rita.mall.ware.dao")  移到config包下的java文件中
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients//如果不在主程序的父包下，需要写路径
public class MallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallWareApplication.class, args);
    }

}
