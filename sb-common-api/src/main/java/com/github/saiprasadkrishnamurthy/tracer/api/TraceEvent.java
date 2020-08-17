package com.github.saiprasadkrishnamurthy.tracer.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceEvent {
    private TraceContext traceContext;
    private String appName;
    private String className;
    private String methodName;
    private long timestamp = System.currentTimeMillis();
    private TraceEventType traceEventType;
    private String host;
    private Map<String, Object> params = new LinkedHashMap<>();
    private Map<String, Object> metadata = new LinkedHashMap<>();
    private List<String> tags = new ArrayList<>();
    private String threadId;
    private long timeTakenInMillis;
}
