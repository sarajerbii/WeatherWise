package com.weatherwise.trip.dto;

import com.weatherwise.trip.entity.TripStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TripRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 500) String description,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        TripStatus status
) {}
