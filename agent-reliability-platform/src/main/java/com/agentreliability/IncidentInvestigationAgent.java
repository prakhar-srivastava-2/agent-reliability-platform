
package com.agentreliability;

import java.util.List;

import com.agentreliability.tools.Deployment;
import com.agentreliability.tools.IncidentMetrics;
import com.agentreliability.tools.LogEntry;
import com.agentreliability.tools.ServiceHealth;
import com.agentreliability.tools.ToolRegistry;

public class IncidentInvestigationAgent {

    private final ToolRegistry toolRegistry;

    public IncidentInvestigationAgent(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }


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

        return new IncidentReport(
                conclusion,
                null,
                null,
                false
        );
    }



}
