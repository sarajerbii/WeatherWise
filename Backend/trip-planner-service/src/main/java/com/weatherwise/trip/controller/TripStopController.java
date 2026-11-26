package com.weatherwise.trip.controller;

import com.weatherwise.trip.dto.StopRequest;
import com.weatherwise.trip.dto.StopResponse;
import com.weatherwise.trip.service.TripStopService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/stops")
public class TripStopController {

    private final TripStopService service;

    public TripStopController(TripStopService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StopResponse add(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId,
                            @Valid @RequestBody StopRequest req) {
        return service.add(jwt.getSubject(), tripId, req);
    }

    @GetMapping
    public List<StopResponse> list(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId) {
        return service.findAll(jwt.getSubject(), tripId);
    }

    @GetMapping("/{stopId}")
    public StopResponse get(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId, @PathVariable Long stopId) {
        return service.findById(jwt.getSubject(), tripId, stopId);
    }

    @PutMapping("/{stopId}")
    public StopResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId,
                               @PathVariable Long stopId, @Valid @RequestBody StopRequest req) {
        return service.update(jwt.getSubject(), tripId, stopId, req);
    }

    @DeleteMapping("/{stopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long tripId, @PathVariable Long stopId) {
        service.delete(jwt.getSubject(), tripId, stopId);
    }
}
