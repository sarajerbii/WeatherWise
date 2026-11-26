package tn.esprit.weatherdataservice.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import tn.esprit.weatherdataservice.model.Granularity;
public record ForecastPeriodDto(@NotNull Instant startTime, @NotNull Instant endTime,
                                @NotNull Granularity granularity) {}
