package com.weatherwise.airquality.controller;

import com.weatherwise.airquality.entity.AirQualityObservation;
import com.weatherwise.airquality.service.AirQualityService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/air-quality")
public class AirQualityController {

    private final AirQualityService service;

    public AirQualityController(AirQualityService service) {
        this.service = service;
    }

    // POST /api/air-quality
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AirQualityObservation create(@Valid @RequestBody AirQualityObservation observation) {
        return service.create(observation);
    }

    // GET /api/air-quality
    @GetMapping
    public List<AirQualityObservation> getAll() {
        return service.getAll();
    }

    // GET /api/air-quality/{id}
    @GetMapping("/{id}")
    public AirQualityObservation getById(@PathVariable String id) {
        return service.getById(id);
    }

    // GET /api/air-quality/city/1
    @GetMapping("/city/{cityId}")
    public List<AirQualityObservation> getByCity(@PathVariable Long cityId) {
        return service.getByCity(cityId);
    }

    // GET /api/air-quality/city/1/current
    @GetMapping("/city/{cityId}/current")
    public AirQualityObservation getCurrent(@PathVariable Long cityId) {
        return service.getCurrent(cityId);
    }

    // GET /api/air-quality/city/1/history?from=2026-11-01T00:00:00&to=2026-11-30T23:59:59
    @GetMapping("/city/{cityId}/history")
    public List<AirQualityObservation> getHistory(
            @PathVariable Long cityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return service.getHistory(cityId, from, to);
    }

    // PUT /api/air-quality/{id}
    @PutMapping("/{id}")
    public AirQualityObservation update(@PathVariable String id,
                                        @Valid @RequestBody AirQualityObservation observation) {
        return service.update(id, observation);
    }

    // DELETE /api/air-quality/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
    // POST /api/air-quality/city/1/refresh?latitude=36.8065&longitude=10.1815
    @PostMapping("/city/{cityId}/refresh")
    public AirQualityObservation refresh(@PathVariable Long cityId,
                                         @RequestParam double latitude,
                                         @RequestParam double longitude) {
        return service.refreshFromExternalApi(cityId, latitude, longitude);
    }
}
