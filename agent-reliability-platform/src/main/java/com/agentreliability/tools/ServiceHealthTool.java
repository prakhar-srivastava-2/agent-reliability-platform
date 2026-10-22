package com.agentreliability.tools;

import java.util.Map;

public class ServiceHealthTool implements DiagnosticTool {

    private final Map<String, ServiceHealth> healthData;

    public ServiceHealthTool(Map<String, ServiceHealth> healthData) {
        this.healthData = Map.copyOf(healthData);
    }

    @Override
    public String getName() {
        return "get_service_health";
    }

    @Override
    public String getDescription() {
        return "Returns the current health status and error rate of a service.";
    }

    @Override
    public Object execute(String serviceName, String timeWindow) {
        return getServiceHealth(serviceName);
    }

    public ServiceHealth getServiceHealth(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException(
                    "Service name must not be blank"
            );
        }

        return healthData.getOrDefault(
                serviceName.toLowerCase(),
                new ServiceHealth(serviceName, "UNKNOWN", null)
        );
    }
}