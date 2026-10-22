
package com.agentreliability.tools;

public class LogEntry {

    private final String serviceName;
    private final String timestamp;
    private final String level;
    private final String message;

    public LogEntry(
            String serviceName,
            String timestamp,
            String level,
            String message) {
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}
