package com.weatherwise.airquality.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.weatherwise.airquality.client.OpenMeteoClient;
import com.weatherwise.airquality.entity.AirQualityIndex;
import com.weatherwise.airquality.entity.AirQualityObservation;
import com.weatherwise.airquality.entity.AqiCategory;
import com.weatherwise.airquality.entity.Pollutant;
import com.weatherwise.airquality.entity.PollutantType;
import com.weatherwise.airquality.exception.ExternalApiException;
import com.weatherwise.airquality.exception.ResourceNotFoundException;
import com.weatherwise.airquality.repository.AirQualityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AirQualityService {

    private final AirQualityRepository repository;
    private final OpenMeteoClient openMeteoClient;

    public AirQualityService(AirQualityRepository repository, OpenMeteoClient openMeteoClient) {
        this.repository = repository;
        this.openMeteoClient = openMeteoClient;
    }

    // ================== CRUD (inchangé) ==================

    public AirQualityObservation create(AirQualityObservation observation) {
        observation.setId(null);
        return repository.save(observation);
    }

    public List<AirQualityObservation> getAll() {
        return repository.findAll();
    }

    public AirQualityObservation getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observation introuvable : " + id));
    }

    public List<AirQualityObservation> getByCity(Long cityId) {
        return repository.findByCityIdOrderByObservedAtDesc(cityId);
    }

    public AirQualityObservation getCurrent(Long cityId) {
        return repository.findFirstByCityIdOrderByObservedAtDesc(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune mesure pour la ville " + cityId));
    }

    public List<AirQualityObservation> getHistory(Long cityId, LocalDateTime from, LocalDateTime to) {
        return repository.findByCityIdAndObservedAtBetween(cityId, from, to);
    }

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

    public void delete(String id) {
        AirQualityObservation existing = getById(id);
        repository.delete(existing);
    }

    // ================== NOUVEAU : appel à l'API externe ==================

    public AirQualityObservation refreshFromExternalApi(Long cityId, double latitude, double longitude) {
        try {
            // 1. Appeler Open-Meteo
            JsonNode current = openMeteoClient.getCurrentAirQuality(latitude, longitude);

            // 2. Créer l'observation
            AirQualityObservation observation = new AirQualityObservation();
            observation.setCityId(cityId);
            observation.setSource("Open-Meteo");
            observation.setObservedAt(LocalDateTime.parse(current.get("time").asText()));

            // 3. Remplir la liste des polluants
            List<Pollutant> pollutants = new ArrayList<>();
            addPollutant(pollutants, PollutantType.PM2_5, current, "pm2_5");
            addPollutant(pollutants, PollutantType.PM10, current, "pm10");
            addPollutant(pollutants, PollutantType.O3, current, "ozone");
            addPollutant(pollutants, PollutantType.NO2, current, "nitrogen_dioxide");
            addPollutant(pollutants, PollutantType.SO2, current, "sulphur_dioxide");
            addPollutant(pollutants, PollutantType.CO, current, "carbon_monoxide");
            observation.setPollutants(pollutants);

            // 4. Calculer l'indice AQI
            if (current.get("us_aqi") == null || current.get("us_aqi").isNull()) {
                throw new ExternalApiException("AQI absent de la réponse Open-Meteo");
            }
            int aqi = current.get("us_aqi").asInt();

            AirQualityIndex index = new AirQualityIndex();
            index.setAqiValue(aqi);
            index.setCategory(getCategory(aqi));
            index.setDominantPollutant(getDominantPollutant(pollutants));
            observation.setAirQualityIndex(index);

            // 5. Enregistrer dans MongoDB
            return repository.save(observation);

        } catch (ExternalApiException e) {
            // Mode dégradé : si l'API est en panne, on renvoie la dernière mesure enregistrée
            Optional<AirQualityObservation> last = repository.findFirstByCityIdOrderByObservedAtDesc(cityId);
            if (last.isPresent()) {
                return last.get();
            }
            throw e; // rien en base : on renvoie l'erreur 502
        }
    }

    // Ajoute un polluant à la liste seulement si la valeur existe dans la réponse
    private void addPollutant(List<Pollutant> list, PollutantType type, JsonNode current, String field) {
        JsonNode value = current.get(field);
        if (value != null && !value.isNull()) {
            list.add(new Pollutant(type, value.asDouble(), "µg/m³"));
        }
    }

    // Catégorie selon la valeur de l'AQI (échelle américaine)
    private AqiCategory getCategory(int aqi) {
        if (aqi <= 50) {
            return AqiCategory.GOOD;
        } else if (aqi <= 100) {
            return AqiCategory.MODERATE;
        } else if (aqi <= 150) {
            return AqiCategory.UNHEALTHY_SENSITIVE;
        } else if (aqi <= 200) {
            return AqiCategory.UNHEALTHY;
        } else if (aqi <= 300) {
            return AqiCategory.VERY_UNHEALTHY;
        } else {
            return AqiCategory.HAZARDOUS;
        }
    }

    // Polluant dominant = celui qui dépasse le plus sa valeur de référence
    private PollutantType getDominantPollutant(List<Pollutant> pollutants) {
        PollutantType dominant = null;
        double maxRatio = 0;

        for (Pollutant p : pollutants) {
            double reference = getReference(p.getType());
            double ratio = p.getValue() / reference;
            if (ratio > maxRatio) {
                maxRatio = ratio;
                dominant = p.getType();
            }
        }
        return dominant;
    }

    private double getReference(PollutantType type) {
        switch (type) {
            case PM2_5: return 15.0;
            case PM10:  return 45.0;
            case O3:    return 100.0;
            case NO2:   return 25.0;
            case SO2:   return 40.0;
            case CO:    return 4000.0;
            default:    return 1.0;
        }
    }
}
