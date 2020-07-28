package com.github.saiprasadkrishnamurthy.tracer.api;

import org.springframework.context.ApplicationContext;

import java.util.function.Function;
import java.util.function.Supplier;

public interface TraceInjector {
    String TRACE_ID_KEY = "X-trace-id";
    Function<ApplicationContext, Void> traceIdInjector();

    default Supplier<String> traceIdSupplier(final ApplicationContext applicationContext) {
        return () -> applicationContext.getBean("defaultState", State.class).getTraceId();
    }

    default void inject(final ApplicationContext applicationContext) {
        traceIdInjector().apply(applicationContext);
    }
}
