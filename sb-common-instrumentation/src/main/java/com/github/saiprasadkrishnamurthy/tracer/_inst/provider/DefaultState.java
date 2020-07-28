package com.github.saiprasadkrishnamurthy.tracer._inst.provider;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceProvider;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
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

    private final ApplicationContext applicationContext;
    private final Map<String, String> store = ExpiringMap.builder()
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
    public String getTraceId() {
        String traceId = applicationContext.getBeansOfType(TraceProvider.class)
                .values().stream()
                .map(TraceProvider::getTraceId)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(UUID.randomUUID().toString());
        store.put(traceId, "");
        Map<String, String> ctx = new HashMap<>();
        ctx.put(TRACE_ID_KEY, traceId);
        MDC.setContextMap(ctx);
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    @Override
    public void propagate(final String traceId) {
        store.put(traceId, "");
        Map<String, String> ctx = new HashMap<>();
        ctx.put(TRACE_ID_KEY, traceId);
        MDC.setContextMap(ctx);
        MDC.put(TRACE_ID_KEY, traceId);
    }
}
