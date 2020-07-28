package com.github.saiprasadkrishnamurthy.tracer._inst.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TraceEvent {
    private String appName;
    private String traceId;
    private String className;
    private String methodName;
    private long timestamp = System.currentTimeMillis();
    private TraceEventType traceEventType;
    private String host;
    private Map<String, Object> params = new LinkedHashMap<>();
    private String threadId;
}
