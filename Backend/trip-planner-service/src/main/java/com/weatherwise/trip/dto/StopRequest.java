package com.weatherwise.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StopRequest(
        @NotBlank @Size(max = 100) String cityName,
        @Size(max = 100) String country,
        @NotNull LocalDate arrivalDate,
        @NotNull LocalDate departureDate
) {}
