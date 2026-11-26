package tn.esprit.weatherdataservice.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.weatherdataservice.dto.WeatherObservationRequest;
import tn.esprit.weatherdataservice.dto.WeatherObservationResponse;
import tn.esprit.weatherdataservice.service.WeatherObservationService;
@RestController
@RequestMapping("/api/weather/observations")
public class WeatherObservationController {
    private final WeatherObservationService service;

    public WeatherObservationController(WeatherObservationService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<WeatherObservationResponse> create(@Valid @RequestBody WeatherObservationRequest r) {
        var saved = service.create(r);
        return ResponseEntity.created(URI.create("/api/weather/observations/" + saved.id()))
                .body(WeatherObservationResponse.from(saved));
    }

    @GetMapping("/{id}")
    public WeatherObservationResponse get(@PathVariable String id) {
        return WeatherObservationResponse.from(service.get(id));
    }

    @GetMapping
    public PagedModel<WeatherObservationResponse> search(
            @RequestParam Long cityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20) Pageable pageable) {
        return new PagedModel<>(service.search(cityId, from, to, pageable)
                .map(WeatherObservationResponse::from));
    }

    @PutMapping("/{id}")
    public WeatherObservationResponse update(@PathVariable String id,
                                             @Valid @RequestBody WeatherObservationRequest r) {
        return WeatherObservationResponse.from(service.update(id, r));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }
}