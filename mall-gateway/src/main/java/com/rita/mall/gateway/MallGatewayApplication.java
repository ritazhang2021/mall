package com.rita.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、开启服务注册发现
 *  (配置nacos的注册中心地址)
 * 2、编写网关配置文件
 * 所有配置都在官方文档中
 https://docs.spring.io/spring-cloud-gateway/docs/3.0.0-SNAPSHOT/reference/html/#gateway-request-predicates-factories
 */
//启用注册发现功能（启动nacos）
@EnableDiscoveryClient
//启动微服务时，排除跟数据源有关的配置，或是在pom中做排队的修改
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallGatewayApplication.class, args);
    }

}
