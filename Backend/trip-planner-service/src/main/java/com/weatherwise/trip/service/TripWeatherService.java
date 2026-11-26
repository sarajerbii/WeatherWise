package com.weatherwise.trip.service;

import com.weatherwise.trip.client.InsightsClient;
import com.weatherwise.trip.client.WeatherDataClient;
import com.weatherwise.trip.dto.*;
import com.weatherwise.trip.entity.RiskLevel;
import com.weatherwise.trip.entity.Trip;
import com.weatherwise.trip.entity.TripStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

/** Prévisions par étape (Weather-Data-Service) et recommandations (Weather-Insights-Service). */
@Service
@Transactional
public class TripWeatherService {

    private static final Logger log = LoggerFactory.getLogger(TripWeatherService.class);

    private final TripService tripService;
    private final WeatherDataClient weatherClient;
    private final InsightsClient insightsClient;
    private final RiskCalculator riskCalculator;

    public TripWeatherService(TripService tripService, WeatherDataClient weatherClient,
                              InsightsClient insightsClient, RiskCalculator riskCalculator) {
        this.tripService = tripService;
        this.weatherClient = weatherClient;
        this.insightsClient = insightsClient;
        this.riskCalculator = riskCalculator;
    }

    /** Met à jour le résumé météo et le niveau de risque de chaque étape. */
    public TripWeatherResponse refreshWeather(String userId, Long tripId, String token) {
        Trip trip = tripService.getOwned(userId, tripId);
        List<StopWeatherResponse> results = new ArrayList<>();

        for (TripStop stop : trip.getStops()) {
            try {
                List<ForecastDto> forecasts = weatherClient.getDailyForecast(
                        stop.getCityName(), stop.getArrivalDate(), stop.getDepartureDate(), token);
                if (forecasts.isEmpty()) {
                    results.add(unavailable(stop, "Aucune prévision disponible pour cette période"));
                    continue;
                }
                stop.setWeatherSummary(riskCalculator.summarize(forecasts));
                stop.setRiskLevel(riskCalculator.evaluate(forecasts));
                results.add(new StopWeatherResponse(stop.getId(), stop.getCityName(),
                        stop.getArrivalDate(), stop.getDepartureDate(),
                        stop.getWeatherSummary(), stop.getRiskLevel(), true, null, forecasts));
            } catch (RestClientException e) {
                log.warn("Prévisions indisponibles pour l'étape {} ({}): {}",
                        stop.getId(), stop.getCityName(), e.getMessage());
                results.add(unavailable(stop, "Service météo indisponible"));
            }
        }
        RiskLevel overall = RiskLevel.max(trip.getStops().stream().map(TripStop::getRiskLevel).toList());
        return new TripWeatherResponse(trip.getId(), overall, results);
    }

    /** Recommandations de voyage, étape par étape. */
    @Transactional(readOnly = true)
    public List<StopRecommendationsResponse> recommendations(String userId, Long tripId, String token) {
        Trip trip = tripService.getOwned(userId, tripId);
        List<StopRecommendationsResponse> results = new ArrayList<>();

        for (TripStop stop : trip.getStops()) {
            try {
                List<ForecastDto> forecasts = weatherClient.getDailyForecast(
                        stop.getCityName(), stop.getArrivalDate(), stop.getDepartureDate(), token);
                if (forecasts.isEmpty()) {
                    results.add(new StopRecommendationsResponse(stop.getId(), stop.getCityName(),
                            null, null, List.of(), "Aucune prévision disponible pour cette période"));
                    continue;
                }
                InsightsResponse insights = insightsClient.getRecommendations(buildRequest(stop, forecasts), token);
                results.add(new StopRecommendationsResponse(stop.getId(), stop.getCityName(),
                        insights.comfortIndex(), insights.riskLevel(),
                        insights.recommendations() == null ? List.of() : insights.recommendations(), null));
            } catch (RestClientException e) {
                log.warn("Recommandations indisponibles pour l'étape {}: {}", stop.getId(), e.getMessage());
                results.add(new StopRecommendationsResponse(stop.getId(), stop.getCityName(),
                        null, null, List.of(), "Service de recommandations indisponible"));
            }
        }
        return results;
    }

    private InsightsRequest buildRequest(TripStop stop, List<ForecastDto> forecasts) {
        double avgTemp = forecasts.stream()
                .filter(f -> f.temperatureMin() != null && f.temperatureMax() != null)
                .mapToDouble(f -> (f.temperatureMin() + f.temperatureMax()) / 2.0)
                .average().orElse(Double.NaN);
        Double maxWind = forecasts.stream().map(ForecastDto::windSpeed)
                .filter(v -> v != null).max(Double::compare).orElse(null);
        Double maxProb = forecasts.stream().map(ForecastDto::precipitationProbability)
                .filter(v -> v != null).max(Double::compare).orElse(null);
        return new InsightsRequest(stop.getCityName(), Double.isNaN(avgTemp) ? null : avgTemp, maxWind, maxProb);
    }

    private StopWeatherResponse unavailable(TripStop s, String message) {
        return new StopWeatherResponse(s.getId(), s.getCityName(), s.getArrivalDate(), s.getDepartureDate(),
                s.getWeatherSummary(), s.getRiskLevel(), false, message, List.of());
    }
}
