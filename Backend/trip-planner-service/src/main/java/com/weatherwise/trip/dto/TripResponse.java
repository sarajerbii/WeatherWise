package com.weatherwise.trip.dto;

import com.weatherwise.trip.entity.TripStatus;

import java.time.LocalDate;

public record TripResponse(Long id, String title, String description,
                           LocalDate startDate, LocalDate endDate, TripStatus status) {}
