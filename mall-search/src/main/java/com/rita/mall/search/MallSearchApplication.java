package com.rita.mall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient//注册在注册中心，别人可以看到它，它也可以看到别人
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MallSearchApplication {

    //https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html

    public static void main(String[] args) {
        SpringApplication.run(MallSearchApplication.class, args);
    }

}
