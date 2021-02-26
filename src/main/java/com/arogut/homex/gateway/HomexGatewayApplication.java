package com.arogut.homex.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
public class HomexGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomexGatewayApplication.class, args);
    }
}
