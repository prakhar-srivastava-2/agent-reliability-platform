
package com.agentreliability.evaluation;

import java.util.ArrayList;
import java.util.List;

import com.agentreliability.EvaluationResult;
import com.agentreliability.IncidentEvaluator;
import com.agentreliability.ReliabilityMetrics;
import com.agentreliability.ReliabilityMetricsCalculator;

public class ReliabilityEvaluationRunner {

    private final IncidentEvaluator evaluator;
    private final ReliabilityMetricsCalculator metricsCalculator;

    public ReliabilityEvaluationRunner(
            IncidentEvaluator evaluator,
            ReliabilityMetricsCalculator metricsCalculator) {
        this.evaluator = evaluator;
        this.metricsCalculator = metricsCalculator;
    }

    public ReliabilityMetrics run(List<EvaluationScenario> scenarios) {
        List<EvaluationResult> results = new ArrayList<>();

        for (EvaluationScenario scenario : scenarios) {
            boolean actualReliable = evaluator.isReportReliable(
                    scenario.getReport(),
                    scenario.getEvidence()
            );

            results.add(new EvaluationResult(
                    scenario.isExpectedReliable(),
                    actualReliable
            ));
        }

        return metricsCalculator.calculate(results);
    }
}
