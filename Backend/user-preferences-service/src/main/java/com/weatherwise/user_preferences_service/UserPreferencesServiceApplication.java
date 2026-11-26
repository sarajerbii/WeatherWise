package com.weatherwise.user_preferences_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserPreferencesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserPreferencesServiceApplication.class, args);
    }

}
