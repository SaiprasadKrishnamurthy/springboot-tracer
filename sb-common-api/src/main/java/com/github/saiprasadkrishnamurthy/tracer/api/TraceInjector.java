package com.github.saiprasadkrishnamurthy.tracer.api;

import org.springframework.context.ApplicationContext;

import java.util.function.Function;

public interface TraceInjector extends TraceContextSupplier {

    Function<ApplicationContext, Void> traceIdInjector();

    default void inject(final ApplicationContext applicationContext) {
        traceIdInjector().apply(applicationContext);
    }
}
