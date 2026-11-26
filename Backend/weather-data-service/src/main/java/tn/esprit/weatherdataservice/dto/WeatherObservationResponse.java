package tn.esprit.weatherdataservice.dto;

import java.time.Instant;
import tn.esprit.weatherdataservice.model.WeatherObservation;
public record WeatherObservationResponse(String id, Long cityId, Instant observedAt,
                                         Double temperature, Double humidity, Double pressure, Double windSpeed,
                                         Double windDirection, String description) {
    public static WeatherObservationResponse from(WeatherObservation o) {
        return new WeatherObservationResponse(o.id(), o.cityId(), o.observedAt(), o.temperature(),
                o.humidity(), o.pressure(), o.windSpeed(), o.windDirection(), o.description());
    }
}
