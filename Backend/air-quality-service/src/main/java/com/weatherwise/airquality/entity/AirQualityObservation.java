package com.weatherwise.airquality.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "air_quality_observations")
public class AirQualityObservation {

    @Id
    private String id;

    @NotNull
    private Long cityId;          // identifiant de la ville (vient du service City)

    @NotNull
    private LocalDateTime observedAt;

    private String source;
    private Double pollenLevel;

    @NotEmpty
    private List<Pollutant> pollutants = new ArrayList<>();

    @NotNull
    private AirQualityIndex airQualityIndex;

    public AirQualityObservation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public LocalDateTime getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(LocalDateTime observedAt) {
        this.observedAt = observedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getPollenLevel() {
        return pollenLevel;
    }

    public void setPollenLevel(Double pollenLevel) {
        this.pollenLevel = pollenLevel;
    }

    public List<Pollutant> getPollutants() {
        return pollutants;
    }

    public void setPollutants(List<Pollutant> pollutants) {
        this.pollutants = pollutants;
    }

    public AirQualityIndex getAirQualityIndex() {
        return airQualityIndex;
    }

    public void setAirQualityIndex(AirQualityIndex airQualityIndex) {
        this.airQualityIndex = airQualityIndex;
    }
}
