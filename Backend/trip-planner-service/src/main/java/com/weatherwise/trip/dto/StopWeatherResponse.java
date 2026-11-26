package com.weatherwise.trip.dto;

import com.weatherwise.trip.entity.RiskLevel;

import java.time.LocalDate;
import java.util.List;

public record StopWeatherResponse(Long stopId, String cityName,
                                  LocalDate arrivalDate, LocalDate departureDate,
                                  String weatherSummary, RiskLevel riskLevel,
                                  boolean forecastAvailable, String message,
                                  List<ForecastDto> forecasts) {}
