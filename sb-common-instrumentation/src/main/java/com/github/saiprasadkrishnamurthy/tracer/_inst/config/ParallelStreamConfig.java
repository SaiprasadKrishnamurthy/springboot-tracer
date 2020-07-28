package com.github.saiprasadkrishnamurthy.tracer._inst.config;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParallelStreamConfig {
    private final State state;


    public ParallelStreamConfig(@Qualifier("defaultState") final State state) {
        this.state = state;
    }

    public String currentTraceId() {
        return state.getTraceId();
    }

    public void propagate(final String traceId) {
        state.propagate(traceId);
    }
}