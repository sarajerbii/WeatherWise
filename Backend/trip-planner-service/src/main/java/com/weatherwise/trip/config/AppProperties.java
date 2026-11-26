package com.weatherwise.trip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weatherwise")
public record AppProperties(Services services, Risk risk) {

    public record Services(String weatherDataUrl, String insightsUrl) {}

    /** Seuils utilisés pour calculer le niveau de risque d'une étape. */
    public record Risk(double heatModerate, double heatHigh, double coldModerate,
                       double windModerate, double windHigh,
                       double rainProbModerate, double rainAmountHigh) {}
}
