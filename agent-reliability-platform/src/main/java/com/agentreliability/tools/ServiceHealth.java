
package com.agentreliability.tools;

public class ServiceHealth {

    private final String serviceName;
    private final String status;
    private final Double errorRate;

    public ServiceHealth(
            String serviceName,
            String status,
            Double errorRate) {
        this.serviceName = serviceName;
        this.status = status;
        this.errorRate = errorRate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStatus() {
        return status;
    }

    public Double getErrorRate() {
        return errorRate;
    }
}
