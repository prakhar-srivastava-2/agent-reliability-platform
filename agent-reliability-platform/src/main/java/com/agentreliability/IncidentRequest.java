
package com.agentreliability;

public class IncidentRequest {

    private final String serviceName;
    private final String timeWindow;

    public IncidentRequest(String serviceName, String timeWindow) {
        this.serviceName = serviceName;
        this.timeWindow = timeWindow;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTimeWindow() {
        return timeWindow;
    }
}
