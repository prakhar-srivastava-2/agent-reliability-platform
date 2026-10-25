package com.agentreliability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class IncidentReportParserTest {

    private final IncidentReportParser parser =
            new IncidentReportParser();

    @Test
    void shouldParseStructuredIncidentReport() {
        String json = """
                {
                  "conclusion": "Database timeouts may cause elevated errors.",
                  "finding": "SUPPORTS_ROOT_CAUSE",
                  "claimedRootCause": "DATABASE_TIMEOUT",
                  "rootCauseConfirmed": false
                }
                """;

        IncidentReport report = parser.parse(json);

        assertEquals(
                "Database timeouts may cause elevated errors.",
                report.getConclusion()
        );
        assertEquals(
                Finding.SUPPORTS_ROOT_CAUSE,
                report.getFinding()
        );
        assertEquals(
                "DATABASE_TIMEOUT",
                report.getClaimedRootCause()
        );
        assertFalse(report.isRootCauseConfirmed());
    }

    @Test
    void shouldParseConfirmedRootCause() {
        String json = """
                {
                  "conclusion": "Database timeout confirmed.",
                  "finding": "SUPPORTS_ROOT_CAUSE",
                  "claimedRootCause": "DATABASE_TIMEOUT",
                  "rootCauseConfirmed": true
                }
                """;

        IncidentReport report = parser.parse(json);

        assertTrue(report.isRootCauseConfirmed());
    }

    @Test
    void shouldRejectMalformedJson() {
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("{ invalid json }")
        );
    }

    @Test
    void shouldRejectUnknownFinding() {
        String json = """
                {
                  "conclusion": "Incident investigated.",
                  "finding": "UNKNOWN_FINDING",
                  "claimedRootCause": "DATABASE_TIMEOUT",
                  "rootCauseConfirmed": false
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(json)
        );
    }

    @Test
    void shouldRejectEmptyInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("")
        );
    }

    @Test
    void shouldRejectMissingConclusion() {
        String json = """
                {
                "finding": "SUPPORTS_ROOT_CAUSE",
                "claimedRootCause": "DATABASE_TIMEOUT",
                "rootCauseConfirmed": false
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(json)
        );
    }

    @Test
    void shouldRejectMissingClaimedRootCause() {
        String json = """
                {
                "conclusion": "Database timeouts observed.",
                "finding": "SUPPORTS_ROOT_CAUSE",
                "rootCauseConfirmed": false
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(json)
        );
    }

    @Test
    void shouldRejectNonBooleanRootCauseConfirmed() {
        String json = """
                {
                "conclusion": "Database timeouts observed.",
                "finding": "SUPPORTS_ROOT_CAUSE",
                "claimedRootCause": "DATABASE_TIMEOUT",
                "rootCauseConfirmed": "false"
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(json)
        );
    }

}