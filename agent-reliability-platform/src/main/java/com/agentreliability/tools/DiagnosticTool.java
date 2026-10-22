package com.agentreliability.tools;

public interface DiagnosticTool {

    String getName();

    String getDescription();

    Object execute(String serviceName, String timeWindow);
}