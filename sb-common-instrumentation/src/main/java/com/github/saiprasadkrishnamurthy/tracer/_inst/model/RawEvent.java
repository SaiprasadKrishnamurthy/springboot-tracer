package com.github.saiprasadkrishnamurthy.tracer._inst.model;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;

import java.util.List;

@AllArgsConstructor
@Data
public class RawEvent {
    private TraceContext traceContext;
    private String appName;
    private MethodInvocation methodInvocation;
    private TraceEventType traceEventType;
    private Throwable err;
    private long timestamp;
    private List<String> tags;
    private String threadId;
    private ApplicationContext applicationContext;
    private long timeTakenInMillis;
}
