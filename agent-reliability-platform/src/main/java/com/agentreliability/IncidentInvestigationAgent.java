
package com.agentreliability;

import java.util.List;

import com.agentreliability.tools.Deployment;
import com.agentreliability.tools.IncidentMetrics;
import com.agentreliability.tools.LogEntry;
import com.agentreliability.tools.ServiceHealth;
import com.agentreliability.tools.ToolRegistry;

public class IncidentInvestigationAgent {

    private final ToolRegistry toolRegistry;
    private final LlmClient llmClient;

    public IncidentInvestigationAgent(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
        this.llmClient = null;
    }

     public IncidentInvestigationAgent(
        ToolRegistry toolRegistry,
        LlmClient llmClient) {
        this.toolRegistry = toolRegistry;
        this.llmClient = llmClient;
     }
    private final IncidentReportParser reportParser = new IncidentReportParser();

    public IncidentReport investigate(IncidentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Incident request cannot be null"
            );
        }

        ServiceHealth health = (ServiceHealth) toolRegistry.executeTool(
                "get_service_health",
                request.getServiceName(),
                request.getTimeWindow()
        );

        IncidentMetrics metrics = (IncidentMetrics) toolRegistry.executeTool(
                "get_incident_metrics",
                request.getServiceName(),
                request.getTimeWindow()
        );

        @SuppressWarnings("unchecked")
        List<LogEntry> logs = (List<LogEntry>) toolRegistry.executeTool(
                "search_logs",
                request.getServiceName(),
                request.getTimeWindow()
        );

        @SuppressWarnings("unchecked")
        List<Deployment> deployments = (List<Deployment>) toolRegistry.executeTool(
                "get_recent_deployments",
                request.getServiceName(),
                request.getTimeWindow()
        );

        if (health == null || "UNKNOWN".equalsIgnoreCase(health.getStatus())) {
            return new IncidentReport(
                    "Unable to determine service health from available data.",
                    null,
                    null,
                    false
            );
        }

        // We have health information, but haven't investigated the root cause yet.
        String conclusion = "Service health status: " + health.getStatus();

        if (logs != null && !logs.isEmpty()) {
            conclusion += ". Found " + logs.size()
            + " log entries for the service.";
        }

        if (deployments != null && !deployments.isEmpty()) {
            Deployment latestDeployment = deployments.get(0);

            conclusion += ". Recent deployment detected: "
                    + latestDeployment.getVersion();
        }

        if (metrics != null && metrics.getErrorRate() > 0.05) {
            conclusion += ". Elevated error rate detected: "
                    + metrics.getErrorRate();
        }
        // String prompt = """
        //         Investigate the following service incident.

        //         Service name: %s
        //         Service health status: %s
        //         Incident metrics: %s
        //         Logs: %s
        //         Recent deployments: %s

        //         Based on this evidence, explain the likely cause of the incident.
        //         Clearly distinguish confirmed facts from hypotheses.
        //         Do not claim a root cause is confirmed unless the evidence proves it.
        //         """.formatted(
        //         request.getServiceName(),
        //         health.getStatus(),
        //         metrics,
        //         logs,
        //         deployments
        // );

        String metricsText = metrics == null
        ? "No metrics available"
        : "Request volume: " + metrics.getRequestVolume()
                + ", Error rate: " + metrics.getErrorRate()
                + ", Average latency: "
                + metrics.getAverageLatencyMs() + " ms";

        String logsText = logs == null || logs.isEmpty()
                ? "No logs available"
                : logs.stream()
                        .map(log -> "Time: " + log.getTimestamp()
                                + ", Level: " + log.getLevel()
                                + ", Message: " + log.getMessage())
                        .collect(java.util.stream.Collectors.joining("\n"));

        String deploymentsText = deployments == null || deployments.isEmpty()
                ? "No recent deployments available"
                : deployments.stream()
                        .map(deployment -> "Version: " + deployment.getVersion()
                                + ", Time: " + deployment.getTimestamp())
                        .collect(java.util.stream.Collectors.joining("\n"));

        String prompt = """
                Investigate the following service incident.

                Service name: %s
                Service health status: %s
                Incident metrics: %s
                Logs:
                %s
                Recent deployments:
                %s

                Based on this evidence, explain the likely cause of the incident.
                Clearly distinguish confirmed facts from hypotheses.
                Do not claim a root cause is confirmed unless the evidence proves it.
                """.formatted(
                request.getServiceName(),
                health.getStatus(),
                metricsText,
                logsText,
                deploymentsText
        );


        if (llmClient != null) {
        String llmResponse = llmClient.generateText(prompt);
        return reportParser.parse(llmResponse);
        }

        return new IncidentReport(
                conclusion,
                null,
                null,
                false
        );
    }

}
