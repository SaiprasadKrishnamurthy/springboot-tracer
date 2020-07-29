package com.github.saiprasadkrishnamurthy.tracer.api;

import org.springframework.context.ApplicationContext;

import java.util.function.Supplier;

public interface TraceContextSupplier {
    String TRACE_ID_KEY = "X-trace-id";
    String TRACE_TAGS_KEY = "X-tags-id";

    default Supplier<TraceContext> traceContextSupplier(final ApplicationContext applicationContext) {
        return () -> applicationContext.getBean("defaultState", State.class).getTraceContext();
    }

}
