
package com.agentreliability;

public class EvaluationResult {

    private final boolean expectedReliable;
    private final boolean actualReliable;

    public EvaluationResult(
            boolean expectedReliable,
            boolean actualReliable) {
        this.expectedReliable = expectedReliable;
        this.actualReliable = actualReliable;
    }

    public boolean isExpectedReliable() {
        return expectedReliable;
    }

    public boolean isActualReliable() {
        return actualReliable;
    }
}
