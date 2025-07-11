package ru.practicum;

import org.junit.jupiter.api.Test;
import ru.practicum.explore.dto.EndpointHitDto;
import ru.practicum.explore.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class StatsClientUnitTest {

    StatsClient client = new StatsClient("http://localhost:9090");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testSendHit() {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp("ewm-main-service");
        hit.setUri("/events/1");
        hit.setIp("192.168.1.1");
        hit.setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER));

        client.sendHit(hit);
    }

    @Test
    public void testGetStats() {
        EndpointHitDto hit = new EndpointHitDto();
        hit.setApp("ewm-test");
        hit.setUri("/events/1");
        hit.setIp("192.168.1.1");
        hit.setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        client.sendHit(hit);

        String start = LocalDateTime.now().minusHours(1).format(DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        List<String> uris = List.of("/events/1");

        List<ViewStatsDto> stats = client.getStats(start, end, uris, false);
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }
}