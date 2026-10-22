package com.agentreliability.tools;

public class IncidentMetrics{

    private final long requestVolume;
    private final double errorRate;
    private final int averageLatencyMs;

    public IncidentMetrics(long requestVolume,double errorRate,int averageLatencyMs){
        this.averageLatencyMs=averageLatencyMs;
        this.errorRate = errorRate;
        this.requestVolume = requestVolume;
    }

    public long getRequestVolume(){
        return requestVolume;
    }
    public double getErrorRate(){
        return errorRate;
    }
    public int getAverageLatencyMs(){
        return averageLatencyMs;
    }
}