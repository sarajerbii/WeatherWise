package com.weatherwise.trip.dto;

import java.util.List;

public record StopRecommendationsResponse(Long stopId, String cityName,
                                          Double comfortIndex, String riskLevel,
                                          List<RecommendationDto> recommendations,
                                          String message) {}
