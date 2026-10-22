
package com.agentreliability.tools;

import java.util.Map;

public class ServiceHealthTool {

    private final Map<String, ServiceHealth> healthData;

    public ServiceHealthTool(Map<String, ServiceHealth> healthData) {
        this.healthData = Map.copyOf(healthData);
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
