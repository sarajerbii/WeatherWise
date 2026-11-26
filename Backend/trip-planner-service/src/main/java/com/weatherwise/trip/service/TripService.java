package com.weatherwise.trip.service;

import com.weatherwise.trip.dto.TripDetailResponse;
import com.weatherwise.trip.dto.TripRequest;
import com.weatherwise.trip.dto.TripResponse;
import com.weatherwise.trip.entity.Trip;
import com.weatherwise.trip.exception.InvalidTripDatesException;
import com.weatherwise.trip.exception.TripNotFoundException;
import com.weatherwise.trip.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TripService {

    private final TripRepository repository;
    private final TripMapper mapper;

    public TripService(TripRepository repository, TripMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public TripResponse create(String userId, TripRequest req) {
        validateDates(req.startDate(), req.endDate());
        Trip trip = new Trip();
        trip.setUserId(userId);
        apply(trip, req);
        return mapper.toResponse(repository.save(trip));
    }

    @Transactional(readOnly = true)
    public List<TripResponse> findAll(String userId) {
        return repository.findByUserIdOrderByStartDateAsc(userId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TripDetailResponse findById(String userId, Long id) {
        return mapper.toDetail(getOwned(userId, id));
    }

    public TripResponse update(String userId, Long id, TripRequest req) {
        validateDates(req.startDate(), req.endDate());
        Trip trip = getOwned(userId, id);
        boolean stopOutside = trip.getStops().stream().anyMatch(s ->
                s.getArrivalDate().isBefore(req.startDate()) || s.getDepartureDate().isAfter(req.endDate()));
        if (stopOutside) {
            throw new InvalidTripDatesException(
                    "Certaines étapes sortent de la nouvelle période du voyage : modifiez-les d'abord");
        }
        apply(trip, req);
        return mapper.toResponse(repository.save(trip));
    }

    public void delete(String userId, Long id) {
        repository.delete(getOwned(userId, id));
    }

    /**
     * Retourne le voyage s'il appartient à l'utilisateur.
     * Un voyage d'un autre utilisateur est traité comme introuvable (404).
     */
    public Trip getOwned(String userId, Long id) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(() -> new TripNotFoundException(id));
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new InvalidTripDatesException("La date de fin doit être postérieure ou égale à la date de début");
        }
    }

    private void apply(Trip t, TripRequest r) {
        t.setTitle(r.title());
        t.setDescription(r.description());
        t.setStartDate(r.startDate());
        t.setEndDate(r.endDate());
        if (r.status() != null) t.setStatus(r.status());
    }
}
