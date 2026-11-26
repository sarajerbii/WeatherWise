package tn.esprit.weatherdataservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "forecasts")
@CompoundIndex(name = "city_period_idx", def = "{'cityId': 1, 'period.startTime': 1}")
public record Forecast(
        @Id String id,
        Long cityId,
        ForecastPeriod period,
        Double temperatureMin,
        Double temperatureMax,
        Double precipitationProbability,
        Double precipitationAmount,
        Double windSpeed,
        String condition) {}