package tn.esprit.weatherdataservice.dto;

import tn.esprit.weatherdataservice.model.Forecast;
public record ForecastResponse(String id, Long cityId, ForecastPeriodDto period,
                               Double temperatureMin, Double temperatureMax, Double precipitationProbability,
                               Double precipitationAmount, Double windSpeed, String condition) {
    public static ForecastResponse from(Forecast f) {
        var p = f.period();
        return new ForecastResponse(f.id(), f.cityId(),
                new ForecastPeriodDto(p.startTime(), p.endTime(), p.granularity()),
                f.temperatureMin(), f.temperatureMax(), f.precipitationProbability(),
                f.precipitationAmount(), f.windSpeed(), f.condition());
    }
}