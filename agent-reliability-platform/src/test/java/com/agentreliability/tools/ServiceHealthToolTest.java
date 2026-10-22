
package com.agentreliability.tools;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ServiceHealthToolTest {

    @Test
    void shouldReturnStoredHealthForExistingService() {

        ServiceHealth checkoutHealth =
                new ServiceHealth("checkout", "DEGRADED", 8.5);

        ServiceHealthTool tool = new ServiceHealthTool(
                Map.of("checkout", checkoutHealth)
        );

        ServiceHealth result = tool.getServiceHealth("checkout");

        assertEquals("checkout", result.getServiceName());
        assertEquals("DEGRADED", result.getStatus());
        assertEquals(8.5, result.getErrorRate());
    }

    @Test
    void shouldReturnStoredHealthRegardlessOfCase() {

        ServiceHealth checkoutHealth =
                new ServiceHealth("checkout", "DEGRADED", 8.5);

        ServiceHealthTool tool = new ServiceHealthTool(
                Map.of("checkout", checkoutHealth)
        );

        ServiceHealth result = tool.getServiceHealth("CHECKOUT");

        assertEquals("checkout", result.getServiceName());
        assertEquals("DEGRADED", result.getStatus());
        assertEquals(8.5, result.getErrorRate());
    }

    @Test
    void shouldReturnUnknownForUnregisteredService() {

        ServiceHealthTool tool = new ServiceHealthTool(
                Map.of(
                        "checkout",
                        new ServiceHealth("checkout", "HEALTHY", 0.2)
                )
        );

        ServiceHealth result = tool.getServiceHealth("inventory");

        assertEquals("inventory", result.getServiceName());
        assertEquals("UNKNOWN", result.getStatus());
        assertNull(result.getErrorRate());
    }

@Test
void shouldRejectNullServiceName() {
    ServiceHealthTool tool = new ServiceHealthTool(Map.of());

    assertThrows(
            IllegalArgumentException.class,
            () -> tool.getServiceHealth(null)
    );
}

    @Test
    void shouldRejectBlankServiceName() {
        ServiceHealthTool tool = new ServiceHealthTool(Map.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> tool.getServiceHealth("   ")
        );
    }

}
