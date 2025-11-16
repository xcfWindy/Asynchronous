package com.example.future;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FutureApplication {

    public static void main(String[] args) {
        SpringApplication.run(FutureApplication.class, args);
    }

}
