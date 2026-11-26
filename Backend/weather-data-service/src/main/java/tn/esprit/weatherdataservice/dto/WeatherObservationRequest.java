package tn.esprit.weatherdataservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;
public record WeatherObservationRequest(
        @NotNull Long cityId,
        @NotNull @PastOrPresent Instant observedAt,
        @NotNull @DecimalMin("-90") @DecimalMax("60") Double temperature,
        @NotNull @DecimalMin("0") @DecimalMax("100") Double humidity,
        @NotNull @Positive Double pressure,
        @NotNull @PositiveOrZero Double windSpeed,
        @DecimalMin("0") @DecimalMax("360") Double windDirection,
        @Size(max = 200) String description) {}