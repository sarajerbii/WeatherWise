package com.weatherwise.airquality.health;

import com.weatherwise.airquality.repository.AirQualityRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

// Apparaît dans /actuator/health sous le nom "airQuality"
@Component
public class AirQualityHealthIndicator implements HealthIndicator {

    private final AirQualityRepository repository;

    public AirQualityHealthIndicator(AirQualityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        try {
            long count = repository.count();
            return Health.up().withDetail("observations", count).build();
        } catch (Exception e) {
            return Health.down().withDetail("erreur", e.getMessage()).build();
        }
    }
}