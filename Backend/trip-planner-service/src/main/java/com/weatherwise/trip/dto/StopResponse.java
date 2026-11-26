package com.weatherwise.trip.dto;

import com.weatherwise.trip.entity.RiskLevel;

import java.time.LocalDate;

public record StopResponse(Long id, String cityName, String country,
                           LocalDate arrivalDate, LocalDate departureDate,
                           String weatherSummary, RiskLevel riskLevel) {}
