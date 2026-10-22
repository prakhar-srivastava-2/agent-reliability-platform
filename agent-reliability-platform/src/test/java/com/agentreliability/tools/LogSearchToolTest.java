package com.agentreliability.tools;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class LogSearchToolTest {

    private final LocalDateTime referenceTime =
            LocalDateTime.parse("2026-10-21T20:00:00");

    @Test
    void shouldReturnOnlyLogsForRequestedService() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "checkout",
                        "2026-10-21T19:30:00",
                        "ERROR",
                        "Checkout failed"
                ),
                new LogEntry(
                        "payment",
                        "2026-10-21T19:35:00",
                        "ERROR",
                        "Payment failed"
                )
        );

        LogSearchTool tool = new LogSearchTool(logs);

        List<LogEntry> results = tool.searchLogs(
                "checkout", "24h", referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("Checkout failed", results.get(0).getMessage());
    }

    @Test
    void shouldMatchServiceNameRegardlessOfCase() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "checkout",
                        "2026-10-21T19:30:00",
                        "ERROR",
                        "Checkout failed"
                )
        );

        LogSearchTool tool = new LogSearchTool(logs);

        List<LogEntry> results = tool.searchLogs(
                "CHECKOUT", "24h", referenceTime
        );

        assertEquals(1, results.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoLogsMatch() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "payment",
                        "2026-10-21T19:30:00",
                        "ERROR",
                        "Payment failed"
                )
        );

        LogSearchTool tool = new LogSearchTool(logs);

        List<LogEntry> results = tool.searchLogs(
                "checkout", "24h", referenceTime
        );

        assertEquals(0, results.size());
    }

    @Test
    void shouldRejectBlankServiceName() {
        LogSearchTool tool = new LogSearchTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.searchLogs(" ", "1h")
        );
    }

    @Test
    void shouldRejectBlankTimeWindow() {
        LogSearchTool tool = new LogSearchTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.searchLogs("checkout", " ")
        );
    }

    @Test
    void shouldExcludeLogsOutsideRequestedTimeWindow() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "checkout",
                        "2026-10-21T19:30:00",
                        "ERROR",
                        "Recent error"
                ),
                new LogEntry(
                        "checkout",
                        "2026-10-21T17:00:00",
                        "ERROR",
                        "Old error"
                )
        );

        LogSearchTool tool = new LogSearchTool(logs);

        List<LogEntry> results = tool.searchLogs(
                "checkout",
                "1h",
                referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("Recent error", results.get(0).getMessage());
    }

    @Test
    void shouldRejectUnsupportedTimeWindow() {
        LogSearchTool tool = new LogSearchTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.searchLogs(
                        "checkout",
                        "3h",
                        referenceTime
                )
        );
    }

    @Test
    void shouldIncludeLogExactlyAtCutoff() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "checkout",
                        "2026-10-21T19:00:00",
                        "ERROR",
                        "Error at cutoff"
                )
        );

        LogSearchTool tool = new LogSearchTool(logs);

        List<LogEntry> results = tool.searchLogs(
                "checkout",
                "1h",
                referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("Error at cutoff", results.get(0).getMessage());
    }

    @Test
    void shouldExcludeFutureDatedLogs() {
        List<LogEntry> logs = List.of(
                new LogEntry(
                        "checkout",
                        "2026-10-21T19:30:00",
                        "ERROR",
                        "Recent error"
                ),
                new LogEntry(
                        "checkout",
                        "2026-10-21T20:05:00",
                        "ERROR",
                        "Future error"
                )
        );

        LogSearchTool tool = new LogSearchTool(logs);

        List<LogEntry> results = tool.searchLogs(
                "checkout",
                "1h",
                referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("Recent error", results.get(0).getMessage());
    }
}