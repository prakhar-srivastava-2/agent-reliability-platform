
package com.agentreliability;

import java.util.List;

public class ReliabilityMetricsCalculator {

    public ReliabilityMetrics calculate(List<EvaluationResult> results) {

        int truePositive = 0;
        int trueNegative = 0;
        int falsePositive = 0;
        int falseNegative = 0;

        for (EvaluationResult result : results) {

            boolean expected = result.isExpectedReliable();
            boolean actual = result.isActualReliable();

            if (expected && actual) {
                truePositive++;
            } else if (!expected && !actual) {
                trueNegative++;
            } else if (!expected) {
                falsePositive++;
            } else {
                falseNegative++;
            }
        }

        return new ReliabilityMetrics(
                truePositive,
                trueNegative,
                falsePositive,
                falseNegative
        );
    }
}
