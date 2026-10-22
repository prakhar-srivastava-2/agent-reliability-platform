
package com.agentreliability.tools;

public class Deployment {

    private final String serviceName;
    private final String version;
    private final String timestamp;

    public Deployment(
            String serviceName,
            String version,
            String timestamp) {
        this.serviceName = serviceName;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
