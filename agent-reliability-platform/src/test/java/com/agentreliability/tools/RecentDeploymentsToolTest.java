package com.agentreliability.tools;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class RecentDeploymentsToolTest {

    private final LocalDateTime referenceTime =
            LocalDateTime.parse("2026-10-21T20:00:00");

    @Test
    void shouldReturnOnlyRecentDeploymentsForRequestedService() {
        List<Deployment> deployments = List.of(
                new Deployment("checkout", "v1.0", "2026-10-21T19:00:00"),
                new Deployment("payment", "v2.0", "2026-10-21T19:30:00"),
                new Deployment("checkout", "v1.1", "2026-10-21T19:45:00")
        );

        RecentDeploymentsTool tool = new RecentDeploymentsTool(deployments);

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "1h", referenceTime
        );

        assertEquals(2, results.size());
        assertEquals("v1.0", results.get(0).getVersion());
        assertEquals("v1.1", results.get(1).getVersion());
    }

    @Test
    void shouldMatchServiceNameRegardlessOfCase() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(new Deployment(
                        "checkout", "v1.0", "2026-10-21T19:30:00"
                ))
        );

        List<Deployment> results = tool.getRecentDeployments(
                "CHECKOUT", "1h", referenceTime
        );

        assertEquals(1, results.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoDeploymentsMatch() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(new Deployment(
                        "payment", "v2.0", "2026-10-21T19:30:00"
                ))
        );

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "1h", referenceTime
        );

        assertEquals(0, results.size());
    }

    @Test
    void shouldRejectBlankServiceName() {
        RecentDeploymentsTool tool =
                new RecentDeploymentsTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getRecentDeployments(
                        " ", "1h", referenceTime
                )
        );
    }

    @Test
    void shouldRejectBlankTimeWindow() {
        RecentDeploymentsTool tool =
                new RecentDeploymentsTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getRecentDeployments(
                        "checkout", " ", referenceTime
                )
        );
    }

    @Test
    void shouldRejectNullReferenceTime() {
        RecentDeploymentsTool tool =
                new RecentDeploymentsTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getRecentDeployments(
                        "checkout", "1h", null
                )
        );
    }

    @Test
    void shouldRejectUnsupportedTimeWindow() {
        RecentDeploymentsTool tool =
                new RecentDeploymentsTool(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getRecentDeployments(
                        "checkout", "3h", referenceTime
                )
        );
    }

    @Test
    void shouldExcludeDeploymentsOutsideTimeWindow() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(
                        new Deployment(
                                "checkout", "old", "2026-10-21T18:59:59"
                        ),
                        new Deployment(
                                "checkout", "recent", "2026-10-21T19:30:00"
                        )
                )
        );

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "1h", referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("recent", results.get(0).getVersion());
    }

    @Test
    void shouldIncludeDeploymentExactlyAtCutoff() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(new Deployment(
                        "checkout", "cutoff", "2026-10-21T19:00:00"
                ))
        );

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "1h", referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("cutoff", results.get(0).getVersion());
    }

    @Test
    void shouldExcludeFutureDatedDeployments() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(new Deployment(
                        "checkout", "future", "2026-10-21T20:00:01"
                ))
        );

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "1h", referenceTime
        );

        assertEquals(0, results.size());
    }

    @Test
    void shouldSupportSixHourWindow() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(
                        new Deployment(
                                "checkout", "inside", "2026-10-21T14:30:00"
                        ),
                        new Deployment(
                                "checkout", "outside", "2026-10-21T13:59:59"
                        )
                )
        );

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "6h", referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("inside", results.get(0).getVersion());
    }

    @Test
    void shouldSupportTwentyFourHourWindow() {
        RecentDeploymentsTool tool = new RecentDeploymentsTool(
                List.of(
                        new Deployment(
                                "checkout", "inside", "2026-10-20T20:00:00"
                        ),
                        new Deployment(
                                "checkout", "outside", "2026-10-20T19:59:59"
                        )
                )
        );

        List<Deployment> results = tool.getRecentDeployments(
                "checkout", "24h", referenceTime
        );

        assertEquals(1, results.size());
        assertEquals("inside", results.get(0).getVersion());
    }
}