package com.agentreliability.tools;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class IncidentMetricsToolTest {

    @Test
    void shouldReturnMetricsForRequestedService() {
        IncidentMetrics metrics = new IncidentMetrics(12000L, 8.5, 850);

        IncidentMetricsTool tool = new IncidentMetricsTool(
                Map.of("checkout", metrics)
        );

        IncidentMetrics result = tool.getIncidentMetrics("checkout");

        assertEquals(12000L, result.getRequestVolume());
        assertEquals(8.5, result.getErrorRate());
        assertEquals(850, result.getAverageLatencyMs());
    }

    @Test
    void shouldMatchServiceNameRegardlessOfCase() {
        IncidentMetrics metrics = new IncidentMetrics(12000L, 8.5, 850);

        IncidentMetricsTool tool = new IncidentMetricsTool(
                Map.of("checkout", metrics)
        );

        IncidentMetrics result = tool.getIncidentMetrics("CHECKOUT");

        assertEquals(metrics, result);
    }

    @Test
    void shouldReturnNullForUnknownService() {
        IncidentMetricsTool tool = new IncidentMetricsTool(
                Map.of("checkout", new IncidentMetrics(12000L, 8.5, 850))
        );

        IncidentMetrics result = tool.getIncidentMetrics("payment");

        assertNull(result);
    }

    @Test
    void shouldRejectNullServiceName() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getIncidentMetrics(null)
        );
    }

    @Test
    void shouldRejectBlankServiceName() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getIncidentMetrics(" ")
        );
    }

    @Test
    void shouldRejectEmptyServiceName() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getIncidentMetrics("")
        );
    }

    @Test
    void shouldReturnNullWhenMetricsMapIsEmpty() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertNull(tool.getIncidentMetrics("checkout"));
    }

    @Test
    void shouldNotBeAffectedByChangesToOriginalMap() {
        Map<String, IncidentMetrics> metricsData =
                new java.util.HashMap<>();

        IncidentMetrics originalMetrics =
                new IncidentMetrics(12000L, 8.5, 850);

        metricsData.put("checkout", originalMetrics);

        IncidentMetricsTool tool =
                new IncidentMetricsTool(metricsData);

        metricsData.put(
                "checkout",
                new IncidentMetrics(500L, 1.0, 100)
        );

        IncidentMetrics result = tool.getIncidentMetrics("checkout");

        assertEquals(12000L, result.getRequestVolume());
        assertEquals(8.5, result.getErrorRate());
        assertEquals(850, result.getAverageLatencyMs());
    }
}