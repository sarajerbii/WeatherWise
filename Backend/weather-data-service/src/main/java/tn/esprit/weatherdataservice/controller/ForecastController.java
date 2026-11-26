package tn.esprit.weatherdataservice.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.weatherdataservice.dto.ForecastRequest;
import tn.esprit.weatherdataservice.dto.ForecastResponse;
import tn.esprit.weatherdataservice.model.Granularity;
import tn.esprit.weatherdataservice.service.ForecastService;
@RestController
@RequestMapping("/api/weather/forecasts")
public class ForecastController {
    private final ForecastService service;

    public ForecastController(ForecastService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ForecastResponse> create(@Valid @RequestBody ForecastRequest r) {
        var saved = service.create(r);
        return ResponseEntity.created(URI.create("/api/weather/forecasts/" + saved.id()))
                .body(ForecastResponse.from(saved));
    }

    @GetMapping("/{id}")
    public ForecastResponse get(@PathVariable String id) { return ForecastResponse.from(service.get(id)); }

    @GetMapping
    public List<ForecastResponse> search(
            @RequestParam Long cityId,
            @RequestParam Granularity granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return service.search(cityId, granularity, from, to).stream().map(ForecastResponse::from).toList();
    }

    @PutMapping("/{id}")
    public ForecastResponse update(@PathVariable String id, @Valid @RequestBody ForecastRequest r) {
        return ForecastResponse.from(service.update(id, r));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }
}