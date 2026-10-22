
package com.agentreliability.tools;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class RecentDeploymentsTool {

    private final List<Deployment> deployments;

    public RecentDeploymentsTool(List<Deployment> deployments) {
        this.deployments = List.copyOf(deployments);
    }
    public List<Deployment> getRecentDeployments(String serviceName,String timeWindow){
        return getRecentDeployments(serviceName, timeWindow, LocalDateTime.now());
    }

    public List<Deployment> getRecentDeployments(String serviceName, String timeWindow,LocalDateTime reference) {

        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException(
                    "Service name must not be blank"
            );
        }
        if (timeWindow==null || timeWindow.isBlank()){
            throw new IllegalArgumentException(
                "Time Window cannot be blank"
            );
        }
        if (reference == null) {
            throw new IllegalArgumentException(
                    "Reference time must not be null"
            );
        }

        Duration duration;
        switch(timeWindow){
            case("1h"):
                duration = Duration.ofHours(1);
                break;
            case("6h"):
                duration = Duration.ofHours(6);
                break;
            case("24h"):
                duration = Duration.ofHours(24);
                break;
            default:
                throw new IllegalArgumentException(
                    "Give a Valid Time"
                );
        }

        LocalDateTime cutoff = reference.minus(duration);

        return deployments.stream()
                .filter(deployment -> deployment.getServiceName()
                        .equalsIgnoreCase(serviceName))
                .filter(deployment ->{
                    LocalDateTime timestamp = LocalDateTime.parse(deployment.getTimestamp());
                        return !timestamp.isBefore(cutoff) && !timestamp.isAfter(reference);

                } )
                .toList();
    }
}
