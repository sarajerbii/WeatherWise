package com.weatherwise.trip.dto;

import com.weatherwise.trip.entity.RiskLevel;
import com.weatherwise.trip.entity.TripStatus;

import java.time.LocalDate;
import java.util.List;

public record TripDetailResponse(Long id, String title, String description,
                                 LocalDate startDate, LocalDate endDate, TripStatus status,
                                 RiskLevel overallRisk, List<StopResponse> stops) {}
