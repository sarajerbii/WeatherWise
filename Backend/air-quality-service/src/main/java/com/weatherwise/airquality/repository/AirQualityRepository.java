package com.weatherwise.airquality.repository;

import com.weatherwise.airquality.entity.AirQualityObservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AirQualityRepository extends MongoRepository<AirQualityObservation, String> {

    // Toutes les mesures d'une ville, la plus récente en premier
    List<AirQualityObservation> findByCityIdOrderByObservedAtDesc(Long cityId);

    // Mesures d'une ville entre deux dates
    List<AirQualityObservation> findByCityIdAndObservedAtBetween(Long cityId, LocalDateTime from, LocalDateTime to);

    // La mesure la plus récente d'une ville
    Optional<AirQualityObservation> findFirstByCityIdOrderByObservedAtDesc(Long cityId);
}
