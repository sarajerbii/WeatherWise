package com.weatherwise.trip.dto;

import com.weatherwise.trip.entity.RiskLevel;

import java.util.List;

public record TripWeatherResponse(Long tripId, RiskLevel overallRisk, List<StopWeatherResponse> stops) {}
