package com.zero.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Zero
 * @Date 2021/7/14 23:40
 * @Since 1.8
 * @Description
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class UAAServer {
    public static void main(String[] args) {
        SpringApplication.run(UAAServer.class, args);
    }
}
