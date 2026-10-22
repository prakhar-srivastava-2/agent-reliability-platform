
package com.agentreliability.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolRegistry {

    private final Map<String, DiagnosticTool> tools;

    public ToolRegistry(List<DiagnosticTool> diagnosticTools) {
        Map<String, DiagnosticTool> registeredTools = new HashMap<>();

        for (DiagnosticTool tool : diagnosticTools) {
            if (registeredTools.containsKey(tool.getName())) {
                throw new IllegalArgumentException(
                        "Duplicate tool name: " + tool.getName()
                );
            }

            registeredTools.put(tool.getName(), tool);
        }

        this.tools = Map.copyOf(registeredTools);
    }

    public DiagnosticTool getTool(String toolName) {
        return tools.get(toolName);
    }

    public Object executeTool(String toolName,String serviceName,String timeWindow){
        if(!tools.containsKey(toolName)){
            throw new IllegalArgumentException(
                "The tool does not exist"
            );
        }
        DiagnosticTool tool = tools.get(toolName);
        return tool.execute(serviceName, timeWindow);
    }
}
