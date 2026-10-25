
package com.agentreliability;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IncidentReportParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public IncidentReport parse(String json) {
        try {
            if (json == null || json.isBlank()) {
                throw new IllegalArgumentException(
                        "Incident report JSON cannot be empty"
                );
            }

            JsonNode root = objectMapper.readTree(json);

            if (root == null || !root.isObject()) {
                throw new IllegalArgumentException(
                        "Incident report must be a JSON object"
                );
            }

            JsonNode conclusionNode = root.get("conclusion");
            JsonNode findingNode = root.get("finding");
            JsonNode rootCauseNode = root.get("claimedRootCause");
            JsonNode confirmedNode = root.get("rootCauseConfirmed");

            if (conclusionNode == null
                    || !conclusionNode.isTextual()
                    || conclusionNode.asText().isBlank()) {
                throw new IllegalArgumentException(
                        "Conclusion must be a non-empty string"
                );
            }

            if (findingNode == null
                    || !findingNode.isTextual()
                    || findingNode.asText().isBlank()) {
                throw new IllegalArgumentException(
                        "Finding must be a non-empty string"
                );
            }

            if (rootCauseNode == null
                    || !rootCauseNode.isTextual()
                    || rootCauseNode.asText().isBlank()) {
                throw new IllegalArgumentException(
                        "Claimed root cause must be a non-empty string"
                );
            }

            if (confirmedNode == null || !confirmedNode.isBoolean()) {
                throw new IllegalArgumentException(
                        "Root cause confirmed must be a boolean"
                );
            }

            Finding finding;
            try {
                finding = Finding.valueOf(findingNode.asText());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Unknown finding: " + findingNode.asText(),
                        e
                );
            }

            return new IncidentReport(
                    conclusionNode.asText(),
                    finding,
                    rootCauseNode.asText(),
                    confirmedNode.asBoolean()
            );

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid incident report JSON",
                    e
            );
        }
    }
}
