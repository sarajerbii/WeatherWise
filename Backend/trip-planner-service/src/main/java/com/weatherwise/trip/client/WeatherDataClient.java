package com.weatherwise.trip.client;

import com.weatherwise.trip.config.AppProperties;
import com.weatherwise.trip.dto.ForecastDto;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/** Appelle le Weather-Data-Service (REST/JSON) en relayant le jeton de l'utilisateur. */
@Component
public class WeatherDataClient {

    private final RestClient client;

    public WeatherDataClient(RestClient.Builder builder, AppProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.client = builder.baseUrl(props.services().weatherDataUrl())
                .requestFactory(factory)
                .build();
    }

    public List<ForecastDto> getDailyForecast(String city, LocalDate from, LocalDate to, String token) {
        ForecastDto[] result = client.get()
                .uri(u -> u.path("/api/weather/forecast")
                        .queryParam("city", city)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .queryParam("granularity", "DAILY")
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(ForecastDto[].class);
        return result == null ? List.of() : Arrays.asList(result);
    }
}
