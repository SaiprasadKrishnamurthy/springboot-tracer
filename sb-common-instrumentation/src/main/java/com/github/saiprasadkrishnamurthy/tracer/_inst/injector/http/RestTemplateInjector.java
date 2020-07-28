package com.github.saiprasadkrishnamurthy.tracer._inst.injector.http;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceInjector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Function;

@Component
public class RestTemplateInjector implements TraceInjector, ApplicationListener<ApplicationReadyEvent> {

    private final State state;

    public RestTemplateInjector(@Qualifier("defaultState") final State state) {
        this.state = state;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        inject(applicationReadyEvent.getApplicationContext());
    }

    @Override
    public Function<ApplicationContext, Void> traceIdInjector() {
        return (applicationContext) -> {
            Map<String, RestTemplate> restTemplateMap = applicationContext.getBeansOfType(RestTemplate.class);
            if (restTemplateMap != null && restTemplateMap.size() > 0) {
                restTemplateMap.values().forEach(rt -> rt.getInterceptors().add((httpRequest, bytes, execution) -> {
                    httpRequest.getHeaders().add(TRACE_ID_KEY, traceContextSupplier(applicationContext).get().getTraceId());
                    httpRequest.getHeaders().add(TRACE_TAGS_KEY, traceContextSupplier(applicationContext).get().getTraceTags());
                    return execution.execute(httpRequest, bytes);
                }));
            }
            return null;
        };
    }
}
