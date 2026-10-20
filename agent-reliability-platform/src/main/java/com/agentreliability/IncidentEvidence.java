
package com.agentreliability;

public class IncidentEvidence {

    private final boolean databaseTimeoutsObserved;
    private final boolean deploymentCorrelationObserved;
    private final String confirmedRootCause;

    public IncidentEvidence(
            boolean databaseTimeoutsObserved,
            boolean deploymentCorrelationObserved,
            String confirmedRootCause) {
        this.databaseTimeoutsObserved = databaseTimeoutsObserved;
        this.deploymentCorrelationObserved = deploymentCorrelationObserved;
        this.confirmedRootCause = confirmedRootCause;
    }

    public boolean isDatabaseTimeoutsObserved() {
        return databaseTimeoutsObserved;
    }

    public boolean isDeploymentCorrelationObserved() {
        return deploymentCorrelationObserved;
    }

    public String getConfirmedRootCause() {
        return confirmedRootCause;
    }
}
