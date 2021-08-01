package com.rita.mall.thridparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * spring web
 * spring cloud routing open feign
 * */
@EnableDiscoveryClient
@SpringBootApplication
public class MallThridPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallThridPartyApplication.class, args);
    }

}
