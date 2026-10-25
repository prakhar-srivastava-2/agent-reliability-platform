
package com.agentreliability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.agentreliability.tools.Deployment;
import com.agentreliability.tools.IncidentMetrics;
import com.agentreliability.tools.IncidentMetricsTool;
import com.agentreliability.tools.LogEntry;
import com.agentreliability.tools.LogSearchTool;
import com.agentreliability.tools.RecentDeploymentsTool;
import com.agentreliability.tools.ServiceHealth;
import com.agentreliability.tools.ServiceHealthTool;
import com.agentreliability.tools.ToolRegistry;

class IncidentInvestigationAgentTest {

    @Test
    void shouldRetrieveServiceHealthThroughAgent() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool =
        new IncidentMetricsTool(Map.of());

        LogSearchTool logTool = new LogSearchTool(List.of());

        RecentDeploymentsTool deploymentTool = new RecentDeploymentsTool(List.of());

        ToolRegistry registry;
        registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );


        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentRequest request = new IncidentRequest("checkout", "1h");

        // Call the agent's actual investigation method.
        agent.investigate(request);
    }

    @Test
    void shouldReturnHealthStatusInReport() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool =
        new IncidentMetricsTool(Map.of());

        LogSearchTool logTool = new LogSearchTool(List.of());

        RecentDeploymentsTool deploymentTool = new RecentDeploymentsTool(List.of());

        ToolRegistry registry;
        registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentReport report = agent.investigate(
                new IncidentRequest("checkout", "1h")
        );

        assertNotNull(report);
        assertEquals(
                "Service health status: DEGRADED",
                report.getConclusion()
        );
        assertNull(report.getClaimedRootCause());
        assertFalse(report.isRootCauseConfirmed());
    }

    @Test
    void shouldAvoidDiagnosisWhenHealthIsUnknown() {
        ServiceHealthTool healthTool =
                new ServiceHealthTool(Map.of());

        IncidentMetricsTool metricsTool =
        new IncidentMetricsTool(Map.of());

        LogSearchTool logTool = new LogSearchTool(List.of());

        RecentDeploymentsTool deploymentTool = new RecentDeploymentsTool(List.of());

        ToolRegistry registry;
        registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentReport report = agent.investigate(
                new IncidentRequest("checkout", "1h")
        );

        assertNotNull(report);
        assertEquals(
                "Unable to determine service health from available data.",
                report.getConclusion()
        );
        assertNull(report.getClaimedRootCause());
        assertFalse(report.isRootCauseConfirmed());
    }

    @Test
    void shouldRetrieveHealthAndMetricsThroughAgent() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool = new IncidentMetricsTool(
                Map.of(
                        "checkout",
                        new IncidentMetrics(1000, 0.15, 500)
                )
        );

        LogSearchTool logTool = new LogSearchTool(List.of());

        RecentDeploymentsTool deploymentTool = new RecentDeploymentsTool(List.of());

        ToolRegistry registry;
        registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentRequest request = new IncidentRequest("checkout", "1h");

        IncidentReport report = agent.investigate(request);

        assertNotNull(report);
        assertEquals(
        "Service health status: DEGRADED. Elevated error rate detected: 0.15",
        report.getConclusion()
    );
        assertTrue(report.getConclusion().contains(
        "Elevated error rate detected: 0.15"
        ));
    }
    @Test
    void shouldIncludeLogCountInReport() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool = new IncidentMetricsTool(Map.of());

        String recentTimestamp = LocalDateTime.now()
            .minusMinutes(30)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String recentTimestamp2 = LocalDateTime.now()
            .minusMinutes(20)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LogSearchTool logTool = new LogSearchTool(
                List.of(
                        new LogEntry(
                                "checkout",
                                recentTimestamp,
                                "ERROR",
                                "Database connection timed out"
                        ),
                        new LogEntry(
                                "checkout",
                                recentTimestamp2,
                                "ERROR",
                                "Request failed"
                        )
                )
        );

        RecentDeploymentsTool deploymentTool = new RecentDeploymentsTool(List.of());

        ToolRegistry registry;
        registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentReport report = agent.investigate(
                new IncidentRequest("checkout", "1h")
        );

        assertNotNull(report);
        assertTrue(report.getConclusion().contains("Found 2 log entries"));
    }

    @Test
    void shouldRetrieveRecentDeploymentsThroughAgent() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool = new IncidentMetricsTool(
                Map.of()
        );

        LogSearchTool logTool = new LogSearchTool(List.of());
        Deployment deployment = new Deployment(
                "checkout",
                "v2.4.1",
                LocalDateTime.now().minusMinutes(20)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        RecentDeploymentsTool deploymentTool =
                new RecentDeploymentsTool(List.of(deployment));

        ToolRegistry registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentReport report = agent.investigate(
                new IncidentRequest("checkout", "1h")
        );

        assertNotNull(report);
        assertTrue(
                report.getConclusion().contains(
                        "Recent deployment detected: v2.4.1"
                )
        );
        assertNull(report.getClaimedRootCause());
        assertFalse(report.isRootCauseConfirmed());
    }

    @Test
    void shouldNotMentionDeploymentWhenNoneExists() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool =
                new IncidentMetricsTool(Map.of());

        LogSearchTool logTool = new LogSearchTool(List.of());

        RecentDeploymentsTool deploymentTool =
                new RecentDeploymentsTool(List.of());

        ToolRegistry registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry);

        IncidentReport report = agent.investigate(
                new IncidentRequest("checkout", "1h")
        );

        assertNotNull(report);
        assertFalse(
                report.getConclusion().contains("Recent deployment detected")
        );
    }

        @Test
        void shouldUseFakeLlmResponseAsReportConclusion() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool = new IncidentMetricsTool(
                Map.of(
                        "checkout",
                        new IncidentMetrics(1000, 0.15, 500)
                )
        );

        LogSearchTool logTool = new LogSearchTool(List.of());
        RecentDeploymentsTool deploymentTool =
                new RecentDeploymentsTool(List.of());

        ToolRegistry registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        String expectedResponse = """
                {
                "conclusion": "Database timeouts may be causing elevated errors.",
                "finding": "SUPPORTS_ROOT_CAUSE",
                "claimedRootCause": "DATABASE_TIMEOUT",
                "rootCauseConfirmed": false
                }
                """;

        FakeLlmClient fakeLlmClient = new FakeLlmClient(expectedResponse);

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry, fakeLlmClient);

        IncidentRequest request = new IncidentRequest("checkout", "1h");
        IncidentReport report = agent.investigate(request);

        assertEquals(
                "Database timeouts may be causing elevated errors.",
                report.getConclusion()
        );
        assertEquals(Finding.SUPPORTS_ROOT_CAUSE, report.getFinding());
        assertEquals("DATABASE_TIMEOUT", report.getClaimedRootCause());
        assertFalse(report.isRootCauseConfirmed());
        }


        @Test
        void shouldIncludeDiagnosticEvidenceInLlmPrompt() {
        ServiceHealthTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "DEGRADED", 0.15)
                )
        );

        IncidentMetricsTool metricsTool = new IncidentMetricsTool(
                Map.of(
                        "checkout",
                        new IncidentMetrics(1000, 0.15, 500)
                )
        );

        LogSearchTool logTool = new LogSearchTool(List.of());

        RecentDeploymentsTool deploymentTool =
                new RecentDeploymentsTool(List.of());

        ToolRegistry registry = new ToolRegistry(
                List.of(healthTool, metricsTool, logTool, deploymentTool)
        );

        FakeLlmClient fakeLlmClient = new FakeLlmClient("""
        {
          "conclusion": "Investigation completed.",
          "finding": "SUPPORTS_ROOT_CAUSE",
          "claimedRootCause": "DATABASE_TIMEOUT",
          "rootCauseConfirmed": false
        }
        """);

        IncidentInvestigationAgent agent =
                new IncidentInvestigationAgent(registry, fakeLlmClient);

        agent.investigate(new IncidentRequest("checkout", "1h"));

        String prompt = fakeLlmClient.getLastPrompt();
        System.out.println("LLM PROMPT:\n" + prompt);
        assertNotNull(prompt);
        assertTrue(prompt.contains("DEGRADED"));
        assertTrue(prompt.contains("checkout"));
        assertTrue(prompt.contains("Recent deployments"));
        assertTrue(prompt.contains("Incident metrics"));
        assertTrue(prompt.contains("Logs"));
        }

}
