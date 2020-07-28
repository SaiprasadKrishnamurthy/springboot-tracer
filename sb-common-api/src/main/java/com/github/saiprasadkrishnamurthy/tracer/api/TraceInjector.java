package com.github.saiprasadkrishnamurthy.tracer.api;

import org.springframework.context.ApplicationContext;

import java.util.function.Function;
import java.util.function.Supplier;

public interface TraceInjector {
    String TRACE_ID_KEY = "X-trace-id";
    String TRACE_TAGS_KEY = "X-tags-id";

    Function<ApplicationContext, Void> traceIdInjector();

    default Supplier<TraceContext> traceContextSupplier(final ApplicationContext applicationContext) {
        return () -> applicationContext.getBean("defaultState", State.class).getTraceContext();
    }

    default void inject(final ApplicationContext applicationContext) {
        traceIdInjector().apply(applicationContext);
    }
}
