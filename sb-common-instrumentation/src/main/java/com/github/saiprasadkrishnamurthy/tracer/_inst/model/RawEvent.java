package com.github.saiprasadkrishnamurthy.tracer._inst.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;

@AllArgsConstructor
@Data
public class RawEvent {
    private String traceId;
    private MethodInvocation methodInvocation;
    private TraceEventType traceEventType;
    private Throwable err;
}
