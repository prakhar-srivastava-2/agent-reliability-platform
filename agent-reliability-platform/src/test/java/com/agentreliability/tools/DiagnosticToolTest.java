package com.agentreliability.tools;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class DiagnosticToolTest {

    @Test
    void shouldImplementDiagnosticToolInterface() {
        ServiceHealthTool tool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "HEALTHY", 0.5)
                )
        );

        assertInstanceOf(DiagnosticTool.class, tool);
    }

    @Test
    void shouldReturnToolName() {
        ServiceHealthTool tool = new ServiceHealthTool(Map.of());

        assertEquals("get_service_health", tool.getName());
    }

    @Test
    void shouldReturnToolDescription() {
        ServiceHealthTool tool = new ServiceHealthTool(Map.of());

        assertNotNull(tool.getDescription());
    }

    @Test
    void shouldExecuteThroughCommonInterface() {
        DiagnosticTool tool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "HEALTHY", 0.5)
                )
        );

        Object result = tool.execute("checkout", null);

        assertInstanceOf(ServiceHealth.class, result);

        ServiceHealth health = (ServiceHealth) result;
        assertEquals("HEALTHY", health.getStatus());
        assertEquals(0.5, health.getErrorRate());
    }
    @Test
    void shouldImplementDiagnosticToolForLogSearch() {
        LogSearchTool tool = new LogSearchTool(List.of());

        assertInstanceOf(DiagnosticTool.class, tool);
    }

    @Test
    void shouldReturnLogSearchToolName() {
        LogSearchTool tool = new LogSearchTool(List.of());

        assertEquals("search_logs", tool.getName());
    }

    @Test
    void shouldReturnLogSearchToolDescription() {
        LogSearchTool tool = new LogSearchTool(List.of());

        assertNotNull(tool.getDescription());
    }

        @Test
    void shouldExecuteLogSearchThroughCommonInterface() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "checkout",
                        LocalDateTime.now()
                                .minusMinutes(30)
                                .withNano(0)
                                .toString(),
                        "ERROR",
                        "Checkout failed"
                )
        );

        DiagnosticTool tool = new LogSearchTool(logs);

        Object result = tool.execute("checkout", "24h");

        List<?> results = assertInstanceOf(List.class, result);

        assertEquals(1, results.size());

        LogEntry entry =
                assertInstanceOf(LogEntry.class, results.get(0));

        assertEquals("Checkout failed", entry.getMessage());
    }

    @Test
    void shouldImplementDiagnosticToolForRecentDeployments() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(List.of());

        assertInstanceOf(DiagnosticTool.class, tool);
    }

    @Test
    void shouldReturnRecentDeploymentsToolName() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(List.of());

        assertEquals("get_recent_deployments", tool.getName());
    }

    @Test
    void shouldReturnRecentDeploymentsToolDescription() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(List.of());

        assertNotNull(tool.getDescription());
    }


    @Test
    void shouldExecuteRecentDeploymentsThroughCommonInterface() {
        List<Deployment> deployments = List.of(
                new Deployment(
                        "checkout",
                        "v2",
                        LocalDateTime.now()
                                .minusMinutes(30)
                                .withNano(0)
                                .toString()
                )
        );

        DiagnosticTool tool = new RecentDeploymentsTool(deployments);

        Object result = tool.execute("checkout", "1h");

        List<?> results = assertInstanceOf(List.class, result);

        assertEquals(1, results.size());

        Deployment deployment =
                assertInstanceOf(Deployment.class, results.get(0));

        assertEquals("v2", deployment.getVersion());
    }


    @Test
    void shouldImplementDiagnosticToolForIncidentMetrics() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertInstanceOf(DiagnosticTool.class, tool);
    }

    @Test
    void shouldReturnIncidentMetricsToolName() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertEquals("get_incident_metrics", tool.getName());
    }

    @Test
    void shouldReturnIncidentMetricsToolDescription() {
        IncidentMetricsTool tool = new IncidentMetricsTool(Map.of());

        assertNotNull(tool.getDescription());
    }

    @Test
    void shouldExecuteIncidentMetricsThroughCommonInterface() {
        IncidentMetrics metrics = new IncidentMetrics(
                1000L,
                2.5,
                250
        );

        DiagnosticTool tool = new IncidentMetricsTool(
                Map.of("checkout", metrics)
        );

        Object result = tool.execute("checkout", null);

        IncidentMetrics actual =
                assertInstanceOf(IncidentMetrics.class, result);

        assertEquals(1000L, actual.getRequestVolume());
        assertEquals(2.5, actual.getErrorRate());
        assertEquals(250, actual.getAverageLatencyMs());
    }
}