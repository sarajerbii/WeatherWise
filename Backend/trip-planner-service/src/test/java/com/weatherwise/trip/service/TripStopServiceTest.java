package com.weatherwise.trip.service;

import com.weatherwise.trip.dto.StopRequest;
import com.weatherwise.trip.dto.StopResponse;
import com.weatherwise.trip.entity.Trip;
import com.weatherwise.trip.entity.TripStop;
import com.weatherwise.trip.exception.InvalidTripDatesException;
import com.weatherwise.trip.repository.TripStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripStopServiceTest {

    @Mock TripService tripService;
    @Mock TripStopRepository stopRepository;
    TripStopService service;
    Trip trip;

    @BeforeEach
    void setUp() {
        service = new TripStopService(tripService, stopRepository, new TripMapper());
        trip = new Trip();
        trip.setUserId("user-1");
        trip.setStartDate(LocalDate.parse("2026-12-10"));
        trip.setEndDate(LocalDate.parse("2026-12-20"));
        when(tripService.getOwned("user-1", 1L)).thenReturn(trip);
    }

    private StopRequest stop(String arrival, String departure) {
        return new StopRequest("Sousse", "Tunisie", LocalDate.parse(arrival), LocalDate.parse(departure));
    }

    @Test
    void addStopInsideTripPeriod() {
        when(stopRepository.save(any(TripStop.class))).thenAnswer(i -> i.getArgument(0));

        StopResponse response = service.add("user-1", 1L, stop("2026-12-11", "2026-12-14"));

        assertThat(response.cityName()).isEqualTo("Sousse");
        assertThat(trip.getStops()).hasSize(1);
    }

    @Test
    void rejectsDepartureBeforeArrival() {
        assertThatThrownBy(() -> service.add("user-1", 1L, stop("2026-12-14", "2026-12-11")))
                .isInstanceOf(InvalidTripDatesException.class);
    }

    @Test
    void rejectsStopOutsideTripPeriod() {
        assertThatThrownBy(() -> service.add("user-1", 1L, stop("2026-12-18", "2026-12-25")))
                .isInstanceOf(InvalidTripDatesException.class);
    }
}
