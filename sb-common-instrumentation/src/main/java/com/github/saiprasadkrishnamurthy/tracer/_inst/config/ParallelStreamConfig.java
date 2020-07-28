package com.github.saiprasadkrishnamurthy.tracer._inst.config;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParallelStreamConfig {
    private final State state;


    public ParallelStreamConfig(@Qualifier("defaultState") final State state) {
        this.state = state;
    }

    public TraceContext currentTraceContext() {
        return state.getTraceContext();
    }

    public void propagate(final TraceContext traceContext) {
        state.propagate(traceContext);
    }
}