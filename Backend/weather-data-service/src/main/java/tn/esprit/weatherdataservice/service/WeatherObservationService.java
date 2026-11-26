package tn.esprit.weatherdataservice.service;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit.weatherdataservice.dto.WeatherObservationRequest;
import tn.esprit.weatherdataservice.exception.ResourceNotFoundException;
import tn.esprit.weatherdataservice.model.WeatherObservation;
import tn.esprit.weatherdataservice.repository.WeatherObservationRepository;
@Service
public class WeatherObservationService {
    private final WeatherObservationRepository repo;

    public WeatherObservationService(WeatherObservationRepository repo) { this.repo = repo; }

    public WeatherObservation create(WeatherObservationRequest r) { return repo.save(toDoc(null, r)); }

    public WeatherObservation get(String id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Observation " + id + " introuvable"));
    }

    public Page<WeatherObservation> search(Long cityId, Instant from, Instant to, Pageable p) {
        if (from != null && to != null)
            return repo.findByCityIdAndObservedAtBetweenOrderByObservedAtDesc(cityId, from, to, p);
        return repo.findByCityIdOrderByObservedAtDesc(cityId, p);
    }

    public WeatherObservation update(String id, WeatherObservationRequest r) {
        get(id);
        return repo.save(toDoc(id, r));
    }

    public void delete(String id) { get(id); repo.deleteById(id); }

    private WeatherObservation toDoc(String id, WeatherObservationRequest r) {
        return new WeatherObservation(id, r.cityId(), r.observedAt(), r.temperature(), r.humidity(),
                r.pressure(), r.windSpeed(), r.windDirection(), r.description());
    }
}