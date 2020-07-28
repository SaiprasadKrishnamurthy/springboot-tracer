package com.github.saiprasadkrishnamurthy.tracer._inst.provider;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceProvider;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("defaultState")
public class DefaultState implements State {

    private static final String TRACE_ID_KEY = TraceProvider.TRACE_ID_KEY;
    private static final String TRACE_TAGS_KEY = TraceProvider.TRACE_TAGS_KEY;

    private final ApplicationContext applicationContext;
    private final Map<TraceContext, String> store = ExpiringMap.builder()
            .expiration(4, TimeUnit.SECONDS)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expirationListener((k, v) -> {
                MDC.remove(k.toString());
                MDC.clear();
                System.out.println(" Cleared " + k + " from Context ");
            })
            .build();

    public DefaultState(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public TraceContext getTraceContext() {
        TraceContext traceContext = applicationContext.getBeansOfType(TraceProvider.class)
                .values().stream()
                .map(TraceProvider::getTraceContext)
                .filter(t -> t.getTraceId() != null || t.getTraceTags() != null)
                .findFirst()
                .map(tc -> {
                    if (tc.getTraceId() == null) {
                        tc.setTraceId(UUID.randomUUID().toString());
                    }
                    return tc;
                }).orElse(new TraceContext(UUID.randomUUID().toString(), null));
        store.put(traceContext, "");
        Map<String, String> ctx = new HashMap<>();
        ctx.put(TRACE_ID_KEY, traceContext.getTraceId());
        ctx.put(TRACE_TAGS_KEY, traceContext.getTraceTags());
        MDC.setContextMap(ctx);
        MDC.put(TRACE_ID_KEY, traceContext.getTraceId());
        MDC.put(TRACE_TAGS_KEY, traceContext.getTraceTags());
        return traceContext;
    }

    @Override
    public void propagate(final TraceContext traceContext) {
        store.put(traceContext, "");
        Map<String, String> ctx = new HashMap<>();
        ctx.put(TRACE_ID_KEY, traceContext.getTraceId());
        MDC.setContextMap(ctx);
        MDC.put(TRACE_ID_KEY, traceContext.getTraceId());
        MDC.put(TRACE_TAGS_KEY, traceContext.getTraceTags());
    }
}
