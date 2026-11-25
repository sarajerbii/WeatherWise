package com.weatherwise.airquality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AirQualityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirQualityServiceApplication.class, args);
    }
}
