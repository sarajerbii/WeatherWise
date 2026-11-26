package com.weatherwise.trip.dto;

/** Corps JSON envoyé au Weather-Insights-Service (Django). */
public record InsightsRequest(String city, Double temperature, Double windSpeed,
                              Double precipitationProbability) {}
