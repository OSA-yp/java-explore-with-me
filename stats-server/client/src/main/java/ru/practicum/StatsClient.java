package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.practicum.exception.StatsServiceException;
import ru.practicum.explore.dto.EndpointHitDto;
import ru.practicum.explore.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
public class StatsClient {
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestClient restClient;

    public StatsClient(@Value("${stats-service.url}") String statsServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsServiceUrl)
                .build();
    }

    public void sendHit(EndpointHitDto hit) {
        try {
            restClient.post()
                    .uri(HIT_ENDPOINT)
                    .body(hit)
                    .retrieve()
                    .toBodilessEntity();
            log.debug("Статистика успешно отправлена");
        } catch (RestClientException e) {
            log.error("Ошибка при отправке статистики: {}", e.getMessage(), e);
            throw new StatsServiceException("Ошибка при отправке статистики", e);
        }
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        validateDateTimeParameters(start, end);

        try {
            log.debug("Запрос статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

            List<ViewStatsDto> stats = restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(STATS_ENDPOINT)
                                .queryParam("start", encodeDateTime(start))
                                .queryParam("end", encodeDateTime(end))
                                .queryParam("unique", Boolean.TRUE.equals(unique));

                        if (uris != null && !uris.isEmpty()) {
                            uriBuilder.queryParam("uris", uris.toArray());
                        }

                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            log.debug("Получено {} записей статистики", stats != null ? stats.size() : 0);
            return stats;
        } catch (RestClientException e) {
            log.error("Ошибка при запросе статистики", e);
            throw new StatsServiceException("Ошибка сервиса статистики", e);
        }
    }

    private void validateDateTimeParameters(String start, String end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Дата и время начала и окончания диапазона должны быть указаны");
        }

        try {
            LocalDateTime startDate = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(end, DATE_TIME_FORMATTER);

            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты. Ожидается yyyy-MM-dd HH:mm:ss", e);
        }
    }

    private String encodeDateTime(String dateTime) {
        return URLEncoder.encode(dateTime, StandardCharsets.UTF_8);
    }
}