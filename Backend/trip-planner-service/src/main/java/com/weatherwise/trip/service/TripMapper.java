package com.weatherwise.trip.service;

import com.weatherwise.trip.dto.StopResponse;
import com.weatherwise.trip.dto.TripDetailResponse;
import com.weatherwise.trip.dto.TripResponse;
import com.weatherwise.trip.entity.RiskLevel;
import com.weatherwise.trip.entity.Trip;
import com.weatherwise.trip.entity.TripStop;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TripMapper {

    public TripResponse toResponse(Trip t) {
        return new TripResponse(t.getId(), t.getTitle(), t.getDescription(),
                t.getStartDate(), t.getEndDate(), t.getStatus());
    }

    public StopResponse toStop(TripStop s) {
        return new StopResponse(s.getId(), s.getCityName(), s.getCountry(),
                s.getArrivalDate(), s.getDepartureDate(), s.getWeatherSummary(), s.getRiskLevel());
    }

    public TripDetailResponse toDetail(Trip t) {
        List<StopResponse> stops = t.getStops().stream().map(this::toStop).toList();
        RiskLevel overall = RiskLevel.max(t.getStops().stream().map(TripStop::getRiskLevel).toList());
        return new TripDetailResponse(t.getId(), t.getTitle(), t.getDescription(),
                t.getStartDate(), t.getEndDate(), t.getStatus(), overall, stops);
    }
}
