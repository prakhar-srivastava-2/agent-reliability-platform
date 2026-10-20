
package com.agentreliability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void shouldRejectConfirmedRootCauseWhenEvidenceDoesNotConfirmIt() {

        IncidentReport report = new IncidentReport(
                "Database is the confirmed root cause.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                true
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldAcceptConfirmedRootCauseWhenEvidenceSupportsIt() {

        IncidentReport report = new IncidentReport(
                "Database is the confirmed root cause.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                true
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                "DATABASE_TIMEOUT"
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertTrue(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldRejectBlankConclusion() {

        IncidentReport report = new IncidentReport(
                "   ",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldRejectNullReport() {

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(null, evidence));
    }

    @Test
    void shouldRejectDatabaseTimeoutClaimWhenNoTimeoutsWereObserved() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                false,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }


    @Test
    void shouldRejectNullEvidence() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, null));
    }

    @Test
    void shouldAcceptDatabaseTimeoutClaimWhenTimeoutsWereObservedButRootCauseIsUnconfirmed() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertTrue(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldRejectUnknownRootCauseLabel() {

        IncidentReport report = new IncidentReport(
                "Checkout failures are caused by an unknown issue.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "MAGIC_FAILURE",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldAcceptDeploymentRegressionAsARecognizedRootCause() {

        IncidentReport report = new IncidentReport(
                "Checkout errors increased after the latest deployment.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DEPLOYMENT_REGRESSION",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                false,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertTrue(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldRejectDeploymentClaimWhenNoDeploymentCorrelationWasObserved() {

        IncidentReport report = new IncidentReport(
                "The deployment may have caused checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DEPLOYMENT_REGRESSION",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                false,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }
    @Test
    void shouldRejectReportWhenConfirmedRootCauseContradictsEvidence() {

        IncidentReport report = new IncidentReport(
                "The deployment is the confirmed root cause.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DEPLOYMENT_REGRESSION",
                true
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                "DATABASE_TIMEOUT"
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }
    @Test
    void shouldRejectConclusionThatContradictsRootCauseClaim() {

        IncidentReport report = new IncidentReport(
                "The payment service is healthy and unrelated.",
                Finding.CONTRADICTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }
    @Test
    void shouldRejectReportWithNullFinding() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                null,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                null
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }

}
