package com.weatherwise.trip.service;

import com.weatherwise.trip.config.AppProperties;
import com.weatherwise.trip.dto.ForecastDto;
import com.weatherwise.trip.entity.RiskLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskCalculatorTest {

    private final RiskCalculator calculator = new RiskCalculator(new AppProperties(
            new AppProperties.Services("", ""),
            new AppProperties.Risk(35, 38, 0, 50, 70, 70, 20)));

    private ForecastDto day(double tMin, double tMax, double prob, double amount, double wind) {
        return new ForecastDto(tMin, tMax, prob, amount, wind, "Ensoleillé");
    }

    @Test
    void mildWeatherIsLow() {
        assertThat(calculator.evaluate(List.of(day(15, 24, 10, 0, 15)))).isEqualTo(RiskLevel.LOW);
    }

    @Test
    void heatAboveModerateThresholdIsModerate() {
        assertThat(calculator.evaluate(List.of(day(22, 36, 0, 0, 10)))).isEqualTo(RiskLevel.MODERATE);
    }

    @Test
    void freezingTemperatureIsModerate() {
        assertThat(calculator.evaluate(List.of(day(-2, 8, 0, 0, 10)))).isEqualTo(RiskLevel.MODERATE);
    }

    @Test
    void strongWindIsHigh() {
        assertThat(calculator.evaluate(List.of(day(15, 22, 10, 0, 80)))).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    void heavyRainIsHigh() {
        assertThat(calculator.evaluate(List.of(day(12, 18, 90, 35, 20)))).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    void worstDayDeterminesRisk() {
        assertThat(calculator.evaluate(List.of(day(15, 24, 10, 0, 15), day(15, 40, 0, 0, 10))))
                .isEqualTo(RiskLevel.HIGH);
    }

    @Test
    void nullValuesAreIgnored() {
        assertThat(calculator.evaluate(List.of(new ForecastDto(null, null, null, null, null, null))))
                .isEqualTo(RiskLevel.LOW);
    }

    @Test
    void summaryContainsKeyFigures() {
        String s = calculator.summarize(List.of(day(10, 20, 30, 1, 25), day(12, 24, 60, 4, 40)));
        assertThat(s).contains("10 à 24 °C").contains("40 km/h").contains("60 %").contains("Ensoleillé");
    }
}
