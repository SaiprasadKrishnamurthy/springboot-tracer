package com.github.saiprasadkrishnamurthy.tracer.api;

public interface State {
    String getTraceId();
    void propagate(String traceId);
}
