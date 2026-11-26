package com.weatherwise.trip.repository;

import com.weatherwise.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserIdOrderByStartDateAsc(String userId);
    Optional<Trip> findByIdAndUserId(Long id, String userId);
}
