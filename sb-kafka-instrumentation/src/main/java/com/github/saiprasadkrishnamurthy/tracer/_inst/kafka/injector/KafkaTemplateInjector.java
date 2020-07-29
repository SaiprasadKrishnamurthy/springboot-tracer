package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceInjector;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class KafkaTemplateInjector implements TraceInjector, ApplicationListener<ApplicationReadyEvent> {

    private final State state;

    @Autowired
    public KafkaTemplateInjector(@Qualifier("defaultState") final State state) {
        this.state = state;
    }

    @Override
    public Function<ApplicationContext, Void> traceIdInjector() {
        return (applicationContext) -> {
            Map<String, KafkaTemplate> kafkaTemplateMap = applicationContext.getBeansOfType(KafkaTemplate.class);
            if (kafkaTemplateMap.size() > 0) {
                kafkaTemplateMap.values().forEach(rt -> {
                    Map configurationProperties = rt.getProducerFactory().getConfigurationProperties();
                    configurationProperties.put("applicationContext", applicationContext);
                    configurationProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerTracer.class.getName());
                });
            }
            return null;
        };
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        inject(applicationReadyEvent.getApplicationContext());
    }
}
