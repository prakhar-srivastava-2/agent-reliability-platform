package com.agentreliability.tools;

import java.util.Map;

public class IncidentMetricsTool implements DiagnosticTool{
    private final Map<String,IncidentMetrics>metricsData;

    public IncidentMetricsTool(Map<String, IncidentMetrics> metricsData){
        this.metricsData = Map.copyOf(metricsData);
    }

    @Override
    public String getName(){
        return "get_incident_metrics";
    }

    @Override
    public String getDescription(){
        return " Returns the metrics for the requested service";
    }

    @Override
    public Object execute(String serviceName,String timeWindow){
        return getIncidentMetrics(serviceName);
    }

    public IncidentMetrics getIncidentMetrics(String serviceName){
        if(serviceName==null || serviceName.isBlank()){
            throw new IllegalArgumentException(
                "Service name can not be blank"
            );
        }
        return metricsData.get(serviceName.toLowerCase());
    }
}
