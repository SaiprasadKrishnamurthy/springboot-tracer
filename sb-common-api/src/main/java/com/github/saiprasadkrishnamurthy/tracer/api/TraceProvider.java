package com.github.saiprasadkrishnamurthy.tracer.api;

public interface TraceProvider {
    String TRACE_ID_KEY = "X-trace-id";
    String getTraceId();
}
