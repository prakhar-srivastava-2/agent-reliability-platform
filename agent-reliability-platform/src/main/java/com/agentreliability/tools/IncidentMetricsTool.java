package com.agentreliability.tools;

import java.util.Map;

public class IncidentMetricsTool{
    private final Map<String,IncidentMetrics>metricsData;

    public IncidentMetricsTool(Map<String, IncidentMetrics> metricsData){
        this.metricsData = Map.copyOf(metricsData);
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
