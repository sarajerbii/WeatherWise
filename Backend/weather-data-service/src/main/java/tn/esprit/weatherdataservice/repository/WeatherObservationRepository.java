package tn.esprit.weatherdataservice.repository;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.weatherdataservice.model.WeatherObservation;
public interface WeatherObservationRepository extends MongoRepository<WeatherObservation, String> {
    Page<WeatherObservation> findByCityIdOrderByObservedAtDesc(Long cityId, Pageable pageable);
    Page<WeatherObservation> findByCityIdAndObservedAtBetweenOrderByObservedAtDesc(
            Long cityId, Instant from, Instant to, Pageable pageable);
}