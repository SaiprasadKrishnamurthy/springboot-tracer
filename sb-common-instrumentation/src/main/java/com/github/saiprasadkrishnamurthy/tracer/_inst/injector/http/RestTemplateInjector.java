package com.github.saiprasadkrishnamurthy.tracer._inst.injector.http;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.github.saiprasadkrishnamurthy.tracer.api.TraceConstants.TRACE_ID_KEY;
import static com.github.saiprasadkrishnamurthy.tracer.api.TraceConstants.TRACE_TAGS_KEY;
import static com.github.saiprasadkrishnamurthy.tracer.api.TraceContextSupplier.traceContextSupplier;

@Component
public class RestTemplateInjector implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {
        inject(applicationReadyEvent.getApplicationContext());
    }

    private void inject(final ApplicationContext applicationContext) {
        Map<String, RestTemplate> restTemplateMap = applicationContext.getBeansOfType(RestTemplate.class);
        if (restTemplateMap != null && restTemplateMap.size() > 0) {
            restTemplateMap.values().forEach(rt -> rt.getInterceptors().add((httpRequest, bytes, execution) -> {
                httpRequest.getHeaders().add(TRACE_ID_KEY, traceContextSupplier(applicationContext).get().getTraceId());
                httpRequest.getHeaders().add(TRACE_TAGS_KEY, traceContextSupplier(applicationContext).get().getTraceTags());
                return execution.execute(httpRequest, bytes);
            }));
        }
    }
}
