package tn.esprit.weatherdataservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
public record ForecastRequest(
        @NotNull Long cityId,
        @NotNull @Valid ForecastPeriodDto period,
        @NotNull Double temperatureMin,
        @NotNull Double temperatureMax,
        @DecimalMin("0") @DecimalMax("100") Double precipitationProbability,
        @PositiveOrZero Double precipitationAmount,
        @PositiveOrZero Double windSpeed,
        @Size(max = 100) String condition) {}