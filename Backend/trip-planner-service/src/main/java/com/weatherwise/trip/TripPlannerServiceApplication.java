package com.weatherwise.trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TripPlannerServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(TripPlannerServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TripPlannerServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner startupInfo(Environment env) {
        return args -> {
            String port = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");

            if (contextPath == null || "/".equals(contextPath.trim())) {
                contextPath = "";
            } else {
                contextPath = contextPath.trim();
                if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;
                if (contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length() - 1);
            }

            String baseUrl = "http://localhost:" + port + contextPath;
            log.info("Backend URL: {}", baseUrl);
            log.info("Swagger URL: {}/swagger-ui/index.html", baseUrl);
            log.info("Repo équipe: https://github.com/sarajerbii/WeatherWise.git");
        };
    }
}
