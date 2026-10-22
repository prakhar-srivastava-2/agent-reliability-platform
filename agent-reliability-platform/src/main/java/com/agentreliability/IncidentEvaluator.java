
package com.agentreliability;

public class IncidentEvaluator {

    public boolean isReportReliable(
            IncidentReport report,
            IncidentEvidence evidence) {

        if (report == null || evidence == null) {
            return false;
        }

        String conclusion = report.getConclusion();
        Finding finding = report.getFinding();
        String claimedRootCause = report.getClaimedRootCause();
        String confirmedRootCause = evidence.getConfirmedRootCause();

        if (conclusion == null || conclusion.isBlank()) {
            return false;
        }

        if (finding == null) {
            return false;
        }

        if (finding == Finding.CONTRADICTS_ROOT_CAUSE) {
            return false;
        }

        if (finding != Finding.SUPPORTS_ROOT_CAUSE) {
            return false;
        }

        if (claimedRootCause == null) {
            return false;
        }

        if (confirmedRootCause != null
                && !claimedRootCause.equals(confirmedRootCause)) {
            return false;
        }

        if (report.isRootCauseConfirmed()
                && confirmedRootCause == null) {
            return false;
        }

        switch (claimedRootCause) {
            case "DATABASE_TIMEOUT":
                if (!evidence.isDatabaseTimeoutsObserved()) {
                    return false;
                }
                break;

            case "DEPLOYMENT_REGRESSION":
                if (!evidence.isDeploymentCorrelationObserved()) {
                    return false;
                }
                break;

            default:
                return false;
        }

        return true;
    }
}
