package com.weatherwise.trip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InsightsResponse(Double comfortIndex, String riskLevel,
                               List<RecommendationDto> recommendations) {}
