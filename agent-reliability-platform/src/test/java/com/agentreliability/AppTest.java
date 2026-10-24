
package com.agentreliability;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.agentreliability.evaluation.EvaluationScenario;
import com.agentreliability.evaluation.ReliabilityEvaluationRunner;

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
    @Test
    void shouldRejectHypothesisThatConflictsWithConfirmedEvidence() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                "DEPLOYMENT_REGRESSION"
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertFalse(evaluator.isReportReliable(report, evidence));
    }

    @Test
    void shouldRejectHypothesisWhenCliamedRootCauseisBlank() {

        IncidentReport report = new IncidentReport(
        "Database timeouts may be contributing to checkout failures.",
        Finding.SUPPORTS_ROOT_CAUSE,
        " ",
        false
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
    void shouldRejectUnconfirmedRootCauseWhenEvidenceConfirmIt() {

        IncidentReport report = new IncidentReport(
                "Database is the confirmed root cause.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                false
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
    void shouldRejectNullClaimedRootCause() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                null,
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
        void shouldAcceptRootCauseClaimWithSurroundingWhitespace() {

        IncidentReport report = new IncidentReport(
                "Database timeouts may be contributing to checkout failures.",
                Finding.SUPPORTS_ROOT_CAUSE,
                " DATABASE_TIMEOUT ",
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
        void shouldAcceptMatchingRootCauseWhenEvidenceHasSurroundingWhitespace() {

        IncidentReport report = new IncidentReport(
                "Database timeouts are the confirmed root cause.",
                Finding.SUPPORTS_ROOT_CAUSE,
                "DATABASE_TIMEOUT",
                true
        );

        IncidentEvidence evidence = new IncidentEvidence(
                true,
                true,
                " DATABASE_TIMEOUT "
        );

        IncidentEvaluator evaluator = new IncidentEvaluator();

        assertTrue(evaluator.isReportReliable(report, evidence));
        }

        @Test
        void shouldCalculateReliabilityMetricsForAllFourOutcomes() {
        List<EvaluationResult> results = List.of(
                new EvaluationResult(true, true),    // TP
                new EvaluationResult(false, false),  // TN
                new EvaluationResult(false, true),   // FP
                new EvaluationResult(true, false)    // FN
        );

        ReliabilityMetricsCalculator calculator =
                new ReliabilityMetricsCalculator();

        ReliabilityMetrics metrics = calculator.calculate(results);

        assertEquals(1, metrics.getTruePositive());
        assertEquals(1, metrics.getTrueNegative());
        assertEquals(1, metrics.getFalsePositive());
        assertEquals(1, metrics.getFalseNegative());

        assertEquals(0.5, metrics.getAccuracy(), 0.0001);
        assertEquals(0.5, metrics.getFalseAcceptanceRate(), 0.0001);
        assertEquals(0.5, metrics.getFalseRejectionRate(), 0.0001);
        }

        @Test
        void shouldReturnZeroMetricsForEmptyResults() {
        ReliabilityMetricsCalculator calculator =
                new ReliabilityMetricsCalculator();

        ReliabilityMetrics metrics =
                calculator.calculate(List.of());

        assertEquals(0, metrics.getTruePositive());
        assertEquals(0, metrics.getTrueNegative());
        assertEquals(0, metrics.getFalsePositive());
        assertEquals(0, metrics.getFalseNegative());

        assertEquals(0.0, metrics.getAccuracy(), 0.0001);
        assertEquals(0.0, metrics.getFalseAcceptanceRate(), 0.0001);
        assertEquals(0.0, metrics.getFalseRejectionRate(), 0.0001);
        }

        @Test
        void shouldCalculateFalseAcceptanceRateWhenAllCasesAreUnreliable() {
        List<EvaluationResult> results = List.of(
                new EvaluationResult(false, true),   // FP
                new EvaluationResult(false, false),  // TN
                new EvaluationResult(false, true)    // FP
        );

        ReliabilityMetricsCalculator calculator =
                new ReliabilityMetricsCalculator();

        ReliabilityMetrics metrics = calculator.calculate(results);

        assertEquals(0, metrics.getTruePositive());
        assertEquals(1, metrics.getTrueNegative());
        assertEquals(2, metrics.getFalsePositive());
        assertEquals(0, metrics.getFalseNegative());

        assertEquals(1.0 / 3.0, metrics.getAccuracy(), 0.0001);
        assertEquals(2.0 / 3.0, metrics.getFalseAcceptanceRate(), 0.0001);
        assertEquals(0.0, metrics.getFalseRejectionRate(), 0.0001);
        }

        @Test
        void shouldCalculateFalseRejectionRateWhenAllCasesAreReliable() {
        List<EvaluationResult> results = List.of(
                new EvaluationResult(true, false), // FN
                new EvaluationResult(true, true),  // TP
                new EvaluationResult(true, false)  // FN
        );

        ReliabilityMetricsCalculator calculator =
                new ReliabilityMetricsCalculator();

        ReliabilityMetrics metrics = calculator.calculate(results);

        assertEquals(1, metrics.getTruePositive());
        assertEquals(0, metrics.getTrueNegative());
        assertEquals(0, metrics.getFalsePositive());
        assertEquals(2, metrics.getFalseNegative());

        assertEquals(1.0 / 3.0, metrics.getAccuracy(), 0.0001);
        assertEquals(0.0, metrics.getFalseAcceptanceRate(), 0.0001);
        assertEquals(2.0 / 3.0, metrics.getFalseRejectionRate(), 0.0001);
        }

        @Test
        void shouldCalculateMetricsAcrossEvaluationScenarios() {
        IncidentEvaluator evaluator = new IncidentEvaluator();
        ReliabilityMetricsCalculator calculator =
                new ReliabilityMetricsCalculator();

        ReliabilityEvaluationRunner runner =
                new ReliabilityEvaluationRunner(evaluator, calculator);

        List<EvaluationScenario> scenarios = List.of(
                new EvaluationScenario(
                        new IncidentReport(
                                "Database timeouts caused the incident",
                                Finding.SUPPORTS_ROOT_CAUSE,
                                "DATABASE_TIMEOUT",
                                false
                        ),
                        new IncidentEvidence(true, false, null),
                        true
                ),
                new EvaluationScenario(
                        new IncidentReport(
                                "Database timeouts caused the incident",
                                Finding.SUPPORTS_ROOT_CAUSE,
                                "DATABASE_TIMEOUT",
                                false
                        ),
                        new IncidentEvidence(false, false, null),
                        false
                ),
                new EvaluationScenario(
                        new IncidentReport(
                                "Deployment caused the incident",
                                Finding.SUPPORTS_ROOT_CAUSE,
                                "DEPLOYMENT_REGRESSION",
                                false
                        ),
                        new IncidentEvidence(false, true, null),
                        true
                ),
                new EvaluationScenario(
                        new IncidentReport(
                                "Unknown cause",
                                Finding.SUPPORTS_ROOT_CAUSE,
                                "UNKNOWN_CAUSE",
                                false
                        ),
                        new IncidentEvidence(false, false, null),
                        false
                )
        );

        ReliabilityMetrics metrics = runner.run(scenarios);

        assertEquals(2, metrics.getTruePositive());
        assertEquals(2, metrics.getTrueNegative());
        assertEquals(0, metrics.getFalsePositive());
        assertEquals(0, metrics.getFalseNegative());

        assertEquals(1.0, metrics.getAccuracy(), 0.0001);
        assertEquals(0.0, metrics.getFalseAcceptanceRate(), 0.0001);
        assertEquals(0.0, metrics.getFalseRejectionRate(), 0.0001);
        }


}
