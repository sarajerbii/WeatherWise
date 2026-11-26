package com.weatherwise.trip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Prévision journalière renvoyée par le Weather-Data-Service (°C, km/h, %, mm). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastDto(Double temperatureMin, Double temperatureMax,
                          Double precipitationProbability, Double precipitationAmount,
                          Double windSpeed, String condition) {}
