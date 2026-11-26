package com.weatherwise.trip.service;

import com.weatherwise.trip.dto.TripRequest;
import com.weatherwise.trip.dto.TripResponse;
import com.weatherwise.trip.entity.Trip;
import com.weatherwise.trip.entity.TripStatus;
import com.weatherwise.trip.entity.TripStop;
import com.weatherwise.trip.exception.InvalidTripDatesException;
import com.weatherwise.trip.exception.TripNotFoundException;
import com.weatherwise.trip.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock TripRepository repository;
    TripService service;

    @BeforeEach
    void setUp() {
        service = new TripService(repository, new TripMapper());
    }

    private TripRequest request(String start, String end) {
        return new TripRequest("Week-end à Tabarka", "Mer", LocalDate.parse(start), LocalDate.parse(end), null);
    }

    @Test
    void createAssignsOwnerAndDefaultStatus() {
        when(repository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        TripResponse response = service.create("user-1", request("2026-12-10", "2026-12-13"));

        ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("user-1");
        assertThat(response.status()).isEqualTo(TripStatus.PLANNED);
        assertThat(response.title()).isEqualTo("Week-end à Tabarka");
    }

    @Test
    void createRejectsEndBeforeStart() {
        assertThatThrownBy(() -> service.create("user-1", request("2026-12-13", "2026-12-10")))
                .isInstanceOf(InvalidTripDatesException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void tripOfAnotherUserIsNotFound() {
        when(repository.findByIdAndUserId(5L, "intrus")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("intrus", 5L)).isInstanceOf(TripNotFoundException.class);
    }

    @Test
    void updateRejectsPeriodThatExcludesAStop() {
        Trip trip = new Trip();
        trip.setUserId("user-1");
        trip.setStartDate(LocalDate.parse("2026-12-10"));
        trip.setEndDate(LocalDate.parse("2026-12-20"));
        TripStop stop = new TripStop();
        stop.setCityName("Tunis");
        stop.setArrivalDate(LocalDate.parse("2026-12-15"));
        stop.setDepartureDate(LocalDate.parse("2026-12-18"));
        trip.addStop(stop);
        when(repository.findByIdAndUserId(1L, "user-1")).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> service.update("user-1", 1L, request("2026-12-10", "2026-12-16")))
                .isInstanceOf(InvalidTripDatesException.class);
    }
}
