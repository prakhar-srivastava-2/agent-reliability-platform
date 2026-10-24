
package com.agentreliability;

public class ReliabilityMetrics {

    private final int truePositive;
    private final int trueNegative;
    private final int falsePositive;
    private final int falseNegative;

    public ReliabilityMetrics(
            int truePositive,
            int trueNegative,
            int falsePositive,
            int falseNegative) {
        this.truePositive = truePositive;
        this.trueNegative = trueNegative;
        this.falsePositive = falsePositive;
        this.falseNegative = falseNegative;
    }

    public int getTruePositive() {
        return truePositive;
    }

    public int getTrueNegative() {
        return trueNegative;
    }

    public int getFalsePositive() {
        return falsePositive;
    }

    public int getFalseNegative() {
        return falseNegative;
    }

    public double getAccuracy() {
        int total = truePositive + trueNegative
                + falsePositive + falseNegative;

        if (total == 0) {
            return 0.0;
        }

        return (double) (truePositive + trueNegative) / total;
    }

    public double getFalseAcceptanceRate() {
        int expectedUnreliable = falsePositive + trueNegative;

        if (expectedUnreliable == 0) {
            return 0.0;
        }

        return (double) falsePositive / expectedUnreliable;
    }

    public double getFalseRejectionRate() {
        int expectedReliable = falseNegative + truePositive;

        if (expectedReliable == 0) {
            return 0.0;
        }

        return (double) falseNegative / expectedReliable;
    }
}
