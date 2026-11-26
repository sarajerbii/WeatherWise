package tn.esprit.weatherdataservice.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "weather_observations")
@CompoundIndex(name = "city_time_idx", def = "{'cityId': 1, 'observedAt': -1}")
public record WeatherObservation(
        @Id String id,
        Long cityId,
        Instant observedAt,
        Double temperature,
        Double humidity,
        Double pressure,
        Double windSpeed,
        Double windDirection,
        String description) {}