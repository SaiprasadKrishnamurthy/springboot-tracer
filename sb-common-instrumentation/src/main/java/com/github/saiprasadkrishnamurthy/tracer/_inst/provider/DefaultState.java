package com.github.saiprasadkrishnamurthy.tracer._inst.provider;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceConstants;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceProvider;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("defaultState")
public class DefaultState implements State {

    private static final String TRACE_ID_KEY = TraceConstants.TRACE_ID_KEY;
    private static final String TRACE_TAGS_KEY = TraceConstants.TRACE_TAGS_KEY;

    private final ApplicationContext applicationContext;

    private final Map<String, List<String>> traceIdAndTags = new ConcurrentHashMap<>();

    private final Map<TraceContext, String> store = ExpiringMap.builder()
            .expiration(5, TimeUnit.SECONDS)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .expirationListener((k, v) -> {
                MDC.remove(k.toString());
                MDC.clear();
                traceIdAndTags.remove(k.toString());
                if (log.isDebugEnabled()) {
                    log.debug(" Cleared {}   from Context ", k);
                }
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
                .filter(t -> t != null && (t.getTraceId() != null || t.getTraceTags() != null))
                .findFirst()
                .map(tc -> {
                    if (tc.getTraceId() == null) {
                        tc.setTraceId(UUID.randomUUID().toString());
                        tc.setMetadata(tc.getMetadata());
                    }
                    return tc;
                }).orElse(new TraceContext(UUID.randomUUID().toString(), null, new HashMap<>()));
        store.put(traceContext, "");
        traceIdAndTags.compute(traceContext.getTraceId(), (k, v) -> {
            if (v == null) {
                List<String> x = new ArrayList<>();
                x.add(traceContext.getTraceTags());
                return x;
            } else {
                v.add(traceContext.getTraceTags());
                return v;
            }
        });
        Map<String, String> ctx = new HashMap<>();
        ctx.put(TRACE_ID_KEY, traceContext.getTraceId());
        ctx.put(TRACE_TAGS_KEY, traceContext.getTraceTags());
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        MDC.setContextMap(ctx);
        MDC.put(TRACE_ID_KEY, traceContext.getTraceId());
        MDC.put(TRACE_TAGS_KEY, traceContext.getTraceTags());
        String tags = new ArrayList<>(traceIdAndTags.get(traceContext.getTraceId())).stream().filter(Objects::nonNull).findFirst().orElse(null);
        traceContext.setTraceTags(tags);
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
        traceIdAndTags.compute(traceContext.getTraceId(), (k, v) -> {
            if (v == null) {
                List<String> x = new ArrayList<>();
                x.add(traceContext.getTraceTags());
                return x;
            } else {
                v.add(traceContext.getTraceTags());
                return v;
            }
        });
    }
}
