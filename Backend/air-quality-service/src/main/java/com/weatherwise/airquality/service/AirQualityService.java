package com.weatherwise.airquality.service;

import com.weatherwise.airquality.entity.AirQualityObservation;
import com.weatherwise.airquality.exception.ResourceNotFoundException;
import com.weatherwise.airquality.repository.AirQualityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirQualityService {

    private final AirQualityRepository repository;

    public AirQualityService(AirQualityRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public AirQualityObservation create(AirQualityObservation observation) {
        observation.setId(null); // MongoDB génère l'id tout seul
        return repository.save(observation);
    }

    // READ : toutes les observations
    public List<AirQualityObservation> getAll() {
        return repository.findAll();
    }

    // READ : une observation par id
    public AirQualityObservation getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observation introuvable : " + id));
    }

    // READ : observations d'une ville
    public List<AirQualityObservation> getByCity(Long cityId) {
        return repository.findByCityIdOrderByObservedAtDesc(cityId);
    }

    // READ : dernière mesure d'une ville
    public AirQualityObservation getCurrent(Long cityId) {
        return repository.findFirstByCityIdOrderByObservedAtDesc(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune mesure pour la ville " + cityId));
    }

    // READ : historique entre deux dates
    public List<AirQualityObservation> getHistory(Long cityId, LocalDateTime from, LocalDateTime to) {
        return repository.findByCityIdAndObservedAtBetween(cityId, from, to);
    }

    // UPDATE
    public AirQualityObservation update(String id, AirQualityObservation newData) {
        AirQualityObservation existing = getById(id);

        existing.setCityId(newData.getCityId());
        existing.setObservedAt(newData.getObservedAt());
        existing.setSource(newData.getSource());
        existing.setPollenLevel(newData.getPollenLevel());
        existing.setPollutants(newData.getPollutants());
        existing.setAirQualityIndex(newData.getAirQualityIndex());

        return repository.save(existing);
    }

    // DELETE
    public void delete(String id) {
        AirQualityObservation existing = getById(id); // 404 si n'existe pas
        repository.delete(existing);
    }
}
