package com.weatherwise.trip.client;

import com.weatherwise.trip.config.AppProperties;
import com.weatherwise.trip.dto.InsightsRequest;
import com.weatherwise.trip.dto.InsightsResponse;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/** Appelle le Weather-Insights-Service (Django) pour les recommandations. */
@Component
public class InsightsClient {

    private final RestClient client;

    public InsightsClient(RestClient.Builder builder, AppProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.client = builder.baseUrl(props.services().insightsUrl())
                .requestFactory(factory)
                .build();
    }

    public InsightsResponse getRecommendations(InsightsRequest request, String token) {
        return client.post()
                .uri("/api/insights/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> {
                    if (StringUtils.hasText(token)) {
                        h.setBearerAuth(token);
                    }
                })
                .body(request)
                .retrieve()
                .body(InsightsResponse.class);
    }
}
