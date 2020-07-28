package com.github.saiprasadkrishnamurthy.tracer._inst.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class RawEvent {
    private String traceId;
    private String appName;
    private MethodInvocation methodInvocation;
    private TraceEventType traceEventType;
    private Throwable err;
    private long timestamp;
    private List<String> tags;
    private String threadId;
}
