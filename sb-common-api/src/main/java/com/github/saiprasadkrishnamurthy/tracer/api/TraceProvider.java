package com.github.saiprasadkrishnamurthy.tracer.api;

public interface TraceProvider {
    String TRACE_ID_KEY = "X-trace-id";
    String TRACE_TAGS_KEY = "X-trace-tags";

    TraceContext getTraceContext();
}
