package com.github.saiprasadkrishnamurthy.tracer.api;

import org.springframework.context.ApplicationContext;

import java.util.function.Supplier;

public class TraceContextSupplier {
    public static Supplier<TraceContext> traceContextSupplier(final ApplicationContext applicationContext) {
        return () -> applicationContext.getBean("defaultState", State.class).getTraceContext();
    }
}
