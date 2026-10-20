
package com.agentreliability;

public class IncidentReport {

    private final String conclusion;
    private final Finding finding;
    private final String claimedRootCause;
    private final boolean rootCauseConfirmed;

    public IncidentReport(
            String conclusion,
            Finding finding,
            String claimedRootCause,
            boolean rootCauseConfirmed) {
        this.conclusion = conclusion;
        this.finding = finding;
        this.claimedRootCause = claimedRootCause;
        this.rootCauseConfirmed = rootCauseConfirmed;
    }

    public String getConclusion() {
        return conclusion;
    }

    public Finding getFinding() {
        return finding;
    }

    public String getClaimedRootCause() {
        return claimedRootCause;
    }

    public boolean isRootCauseConfirmed() {
        return rootCauseConfirmed;
    }
}
