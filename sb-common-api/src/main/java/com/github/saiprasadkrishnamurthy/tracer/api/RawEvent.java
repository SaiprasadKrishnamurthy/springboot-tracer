package com.github.saiprasadkrishnamurthy.tracer.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class RawEvent {
    private TraceContext traceContext;
    private String appName;
    private MethodInvocation methodInvocation;
    private TraceEventType traceEventType;
    private Throwable err;
    private long start;
    private long end;
    private List<String> tags = new ArrayList<>();
    private String threadId;
    private ApplicationContext applicationContext;
    private long timeTakenInMillis;
}
