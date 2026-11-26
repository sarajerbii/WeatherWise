package com.weatherwise.trip.controller;

import com.weatherwise.trip.dto.*;
import com.weatherwise.trip.service.TripService;
import com.weatherwise.trip.service.TripWeatherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final TripWeatherService weatherService;

    public TripController(TripService tripService, TripWeatherService weatherService) {
        this.tripService = tripService;
        this.weatherService = weatherService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TripRequest req) {
        return tripService.create(jwt.getSubject(), req);
    }

    @GetMapping
    public List<TripResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return tripService.findAll(jwt.getSubject());
    }

    @GetMapping("/{id}")
    public TripDetailResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return tripService.findById(jwt.getSubject(), id);
    }

    @PutMapping("/{id}")
    public TripResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                               @Valid @RequestBody TripRequest req) {
        return tripService.update(jwt.getSubject(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        tripService.delete(jwt.getSubject(), id);
    }

    /** Résumé météo + niveau de risque de chaque étape (appelle le Weather-Data-Service). */
    @GetMapping("/{id}/weather")
    public TripWeatherResponse weather(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return weatherService.refreshWeather(jwt.getSubject(), id, jwt.getTokenValue());
    }

    /** Recommandations par étape (appelle le Weather-Insights-Service). */
    @GetMapping("/{id}/recommendations")
    public List<StopRecommendationsResponse> recommendations(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return weatherService.recommendations(jwt.getSubject(), id, jwt.getTokenValue());
    }
}
