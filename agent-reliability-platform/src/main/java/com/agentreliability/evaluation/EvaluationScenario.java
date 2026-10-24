
package com.agentreliability.evaluation;

import com.agentreliability.IncidentEvidence;
import com.agentreliability.IncidentReport;

public class EvaluationScenario{

    private final IncidentReport report;
    private final IncidentEvidence evidence;
    private final boolean expectedReliable;

    public EvaluationScenario(
            IncidentReport report,
            IncidentEvidence evidence,
            boolean expectedReliable) {
        this.report = report;
        this.evidence = evidence;
        this.expectedReliable = expectedReliable;
    }

    public IncidentReport getReport() {
        return report;
    }

    public IncidentEvidence getEvidence() {
        return evidence;
    }

    public boolean isExpectedReliable() {
        return expectedReliable;
    }
}
