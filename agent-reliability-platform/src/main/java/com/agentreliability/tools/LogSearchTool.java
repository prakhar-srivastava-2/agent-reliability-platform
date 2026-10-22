
package com.agentreliability.tools;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class LogSearchTool implements DiagnosticTool{

    private final List<LogEntry> logs;

    public LogSearchTool(List<LogEntry> logs) {
    this.logs = List.copyOf(logs);
    }

    @Override
    public String getName(){
        return "search_logs";
    }

    @Override
    public String getDescription(){
        return "Searches logs for a service within a specified time window.";
    }

    @Override
    public Object execute(String serviceName, String timeWindow){
        return searchLogs(serviceName, timeWindow);
    }


    // Normal application method: uses the actual current time
    public List<LogEntry> searchLogs(
            String serviceName,
            String timeWindow) {

        return searchLogs(
                serviceName,
                timeWindow,
                LocalDateTime.now()
        );
    }

    // Testable method: accepts a fixed reference time
    List<LogEntry> searchLogs(
            String serviceName,
            String timeWindow,
            LocalDateTime referenceTime) {

        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException(
                    "Service name must not be blank"
            );
        }

        if (timeWindow == null || timeWindow.isBlank()) {
            throw new IllegalArgumentException(
                    "Time window must not be blank"
            );
        }

        if (referenceTime == null) {
            throw new IllegalArgumentException(
                    "Reference time must not be null"
            );
        }

        Duration duration;

        switch (timeWindow) {
            case "1h":
                duration = Duration.ofHours(1);
                break;

            case "6h":
                duration = Duration.ofHours(6);
                break;

            case "24h":
                duration = Duration.ofHours(24);
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported time window: " + timeWindow
                );
        }

        LocalDateTime cutoff = referenceTime.minus(duration);

        return logs.stream()
                .filter(log -> log.getServiceName()
                        .equalsIgnoreCase(serviceName))
                .filter(log -> {
                    LocalDateTime timestamp =
                            LocalDateTime.parse(log.getTimestamp());

                    return !timestamp.isBefore(cutoff)
                            && !timestamp.isAfter(referenceTime);
                })
                .toList();
    }
}
