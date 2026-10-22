
package com.agentreliability.tools;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ToolRegistryTest {

    @Test
    void shouldRegisterAndRetrieveToolByName() {
        DiagnosticTool healthTool =
                new ServiceHealthTool(Map.of());

        ToolRegistry registry =
                new ToolRegistry(List.of(healthTool));

        DiagnosticTool result =
                registry.getTool("get_service_health");

        assertSame(healthTool, result);
    }

    @Test
    void shouldRegisterMultipleTools() {
        DiagnosticTool healthTool =
                new ServiceHealthTool(Map.of());

        DiagnosticTool metricsTool =
                new IncidentMetricsTool(Map.of());

        ToolRegistry registry =
                new ToolRegistry(List.of(healthTool, metricsTool));

        assertSame(
                healthTool,
                registry.getTool("get_service_health")
        );

        assertSame(
                metricsTool,
                registry.getTool("get_incident_metrics")
        );
    }

    @Test
    void shouldReturnNullForUnknownToolName() {
        ToolRegistry registry =
                new ToolRegistry(List.of(
                        new ServiceHealthTool(Map.of())
                ));

        assertNull(registry.getTool("unknown_tool"));
    }

    @Test
    void shouldRejectDuplicateToolNames() {
        DiagnosticTool firstTool =
                new ServiceHealthTool(Map.of());

        DiagnosticTool secondTool =
                new ServiceHealthTool(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> new ToolRegistry(
                        List.of(firstTool, secondTool)
                )
        );
    }
    @Test
    void shouldExecuteRegisteredTool() {
        DiagnosticTool healthTool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "HEALTHY", 0.5)
                )
        );

        ToolRegistry registry =
                new ToolRegistry(List.of(healthTool));

        Object result = registry.executeTool(
                "get_service_health",
                "checkout",
                null
        );

        ServiceHealth health =
                assertInstanceOf(ServiceHealth.class, result);

        assertEquals("HEALTHY", health.getStatus());
        assertEquals(0.5, health.getErrorRate());
    }

    @Test
    void shouldThrowExceptionWhenExecutingUnknownTool() {
        ToolRegistry registry = new ToolRegistry(List.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.executeTool(
                        "unknown_tool",
                        "checkout",
                        null
                )
        );
    }
}
