package com.weatherwise.trip.service;

import com.weatherwise.trip.config.AppProperties;
import com.weatherwise.trip.dto.ForecastDto;
import com.weatherwise.trip.entity.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Calcule le niveau de risque et le résumé météo d'une étape à partir des prévisions journalières. */
@Component
public class RiskCalculator {

    private final AppProperties.Risk risk;

    public RiskCalculator(AppProperties props) {
        this.risk = props.risk();
    }

    public RiskLevel evaluate(List<ForecastDto> forecasts) {
        RiskLevel level = RiskLevel.LOW;
        for (ForecastDto f : forecasts) {
            double tMax = val(f.temperatureMax());
            double tMin = val(f.temperatureMin());
            double wind = val(f.windSpeed());
            double prob = val(f.precipitationProbability());
            double amount = val(f.precipitationAmount());

            boolean high = tMax >= risk.heatHigh()
                    || wind >= risk.windHigh()
                    || (prob >= risk.rainProbModerate() && amount >= risk.rainAmountHigh());
            boolean moderate = tMax >= risk.heatModerate()
                    || tMin <= risk.coldModerate()
                    || wind >= risk.windModerate()
                    || prob >= risk.rainProbModerate();

            if (high) return RiskLevel.HIGH;
            if (moderate) level = RiskLevel.MODERATE;
        }
        return level;
    }

    public String summarize(List<ForecastDto> forecasts) {
        double tMin = Double.NaN, tMax = Double.NaN, wind = Double.NaN, prob = Double.NaN;
        String condition = null;
        for (ForecastDto f : forecasts) {
            tMin = min(tMin, val(f.temperatureMin()));
            tMax = max(tMax, val(f.temperatureMax()));
            wind = max(wind, val(f.windSpeed()));
            prob = max(prob, val(f.precipitationProbability()));
            if (condition == null && f.condition() != null && !f.condition().isBlank()) {
                condition = f.condition();
            }
        }
        List<String> parts = new ArrayList<>();
        if (!Double.isNaN(tMin) && !Double.isNaN(tMax)) {
            parts.add(String.format(Locale.ROOT, "Température %.0f à %.0f °C", tMin, tMax));
        } else if (!Double.isNaN(tMax)) {
            parts.add(String.format(Locale.ROOT, "Température max %.0f °C", tMax));
        }
        if (!Double.isNaN(wind)) parts.add(String.format(Locale.ROOT, "vent jusqu'à %.0f km/h", wind));
        if (!Double.isNaN(prob)) parts.add(String.format(Locale.ROOT, "pluie jusqu'à %.0f %%", prob));
        if (condition != null) parts.add("conditions : " + condition);
        String summary = String.join(", ", parts);
        return summary.length() > 500 ? summary.substring(0, 500) : summary;
    }

    private static double val(Double v) { return v == null ? Double.NaN : v; }

    private static double min(double a, double b) {
        if (Double.isNaN(a)) return b;
        if (Double.isNaN(b)) return a;
        return Math.min(a, b);
    }

    private static double max(double a, double b) {
        if (Double.isNaN(a)) return b;
        if (Double.isNaN(b)) return a;
        return Math.max(a, b);
    }
}
