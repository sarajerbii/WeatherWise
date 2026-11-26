package tn.esprit.weatherdataservice.model;

import java.time.Instant;

public record ForecastPeriod(Instant startTime, Instant endTime, Granularity granularity) {}