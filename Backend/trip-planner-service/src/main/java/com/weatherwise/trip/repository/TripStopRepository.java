package com.weatherwise.trip.repository;

import com.weatherwise.trip.entity.TripStop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripStopRepository extends JpaRepository<TripStop, Long> {
    Optional<TripStop> findByIdAndTripId(Long id, Long tripId);
}
