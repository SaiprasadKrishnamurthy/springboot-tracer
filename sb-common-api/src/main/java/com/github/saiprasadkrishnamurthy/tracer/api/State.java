package com.github.saiprasadkrishnamurthy.tracer.api;

public interface State {
    TraceContext getTraceContext();
    void propagate(TraceContext traceContext);
}
