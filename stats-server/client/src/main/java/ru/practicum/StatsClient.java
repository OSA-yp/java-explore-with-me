package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.explore.dto.EndpointHitDto;
import ru.practicum.explore.dto.ViewStatsDto;

import java.util.List;

@Component
public class StatsClient {
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";

    private final RestClient restClient;

    public StatsClient(@Value("${stats-service.url}") String statsServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsServiceUrl)
                .build();
    }

    public void sendHit(EndpointHitDto hit) {
        restClient.post()
                .uri(HIT_ENDPOINT)
                .body(hit)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Дата и время начала и окончания диапазона должны быть указаны");
        }

        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(STATS_ENDPOINT)
                            .queryParam("start", start)
                            .queryParam("end", end);

                    if (uris != null && !uris.isEmpty()) {
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    }
                    if (unique != null) {
                        uriBuilder.queryParam("unique", unique);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}