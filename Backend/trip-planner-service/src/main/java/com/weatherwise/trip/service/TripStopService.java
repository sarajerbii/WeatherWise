package com.weatherwise.trip.service;

import com.weatherwise.trip.dto.StopRequest;
import com.weatherwise.trip.dto.StopResponse;
import com.weatherwise.trip.entity.Trip;
import com.weatherwise.trip.entity.TripStop;
import com.weatherwise.trip.exception.InvalidTripDatesException;
import com.weatherwise.trip.exception.StopNotFoundException;
import com.weatherwise.trip.repository.TripStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TripStopService {

    private final TripService tripService;
    private final TripStopRepository stopRepository;
    private final TripMapper mapper;

    public TripStopService(TripService tripService, TripStopRepository stopRepository, TripMapper mapper) {
        this.tripService = tripService;
        this.stopRepository = stopRepository;
        this.mapper = mapper;
    }

    public StopResponse add(String userId, Long tripId, StopRequest req) {
        Trip trip = tripService.getOwned(userId, tripId);
        validate(trip, req);
        TripStop stop = new TripStop();
        apply(stop, req);
        trip.addStop(stop);
        return mapper.toStop(stopRepository.save(stop));
    }

    @Transactional(readOnly = true)
    public List<StopResponse> findAll(String userId, Long tripId) {
        return tripService.getOwned(userId, tripId).getStops().stream().map(mapper::toStop).toList();
    }

    @Transactional(readOnly = true)
    public StopResponse findById(String userId, Long tripId, Long stopId) {
        return mapper.toStop(getStop(userId, tripId, stopId));
    }

    public StopResponse update(String userId, Long tripId, Long stopId, StopRequest req) {
        Trip trip = tripService.getOwned(userId, tripId);
        validate(trip, req);
        TripStop stop = trip.getStops().stream()
                .filter(s -> s.getId().equals(stopId)).findFirst()
                .orElseThrow(() -> new StopNotFoundException(stopId));
        apply(stop, req);
        // La ville ou les dates ont pu changer : l'ancien résumé n'est plus valable
        stop.setWeatherSummary(null);
        stop.setRiskLevel(null);
        return mapper.toStop(stopRepository.save(stop));
    }

    public void delete(String userId, Long tripId, Long stopId) {
        Trip trip = tripService.getOwned(userId, tripId);
        TripStop stop = trip.getStops().stream()
                .filter(s -> s.getId().equals(stopId)).findFirst()
                .orElseThrow(() -> new StopNotFoundException(stopId));
        trip.removeStop(stop); // orphanRemoval supprime l'étape
    }

    private TripStop getStop(String userId, Long tripId, Long stopId) {
        Trip trip = tripService.getOwned(userId, tripId);
        return stopRepository.findByIdAndTripId(stopId, trip.getId())
                .orElseThrow(() -> new StopNotFoundException(stopId));
    }

    private void validate(Trip trip, StopRequest req) {
        if (req.departureDate().isBefore(req.arrivalDate())) {
            throw new InvalidTripDatesException("La date de départ doit être postérieure ou égale à la date d'arrivée");
        }
        if (req.arrivalDate().isBefore(trip.getStartDate()) || req.departureDate().isAfter(trip.getEndDate())) {
            throw new InvalidTripDatesException(
                    "Les dates de l'étape doivent être comprises dans la période du voyage ("
                            + trip.getStartDate() + " → " + trip.getEndDate() + ")");
        }
    }

    private void apply(TripStop s, StopRequest r) {
        s.setCityName(r.cityName());
        s.setCountry(r.country());
        s.setArrivalDate(r.arrivalDate());
        s.setDepartureDate(r.departureDate());
    }
}
