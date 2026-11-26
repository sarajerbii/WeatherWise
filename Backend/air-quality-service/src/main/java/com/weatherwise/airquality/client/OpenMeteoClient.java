package com.weatherwise.airquality.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.weatherwise.airquality.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// Cette classe appelle l'API externe Open-Meteo (gratuite, sans clé)
@Component
public class OpenMeteoClient {

    private static final String BASE_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";

    private final RestTemplate restTemplate = new RestTemplate();

    // Retourne le bloc "current" de la réponse JSON
    public JsonNode getCurrentAirQuality(double latitude, double longitude) {
        String url = BASE_URL
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone";

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response == null || response.get("current") == null) {
                throw new ExternalApiException("Réponse invalide de l'API Open-Meteo");
            }
            return response.get("current");

        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            // API en panne, pas d'internet, quota dépassé...
            throw new ExternalApiException("API Open-Meteo indisponible : " + e.getMessage());
        }
    }
}
