package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceInjector;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class KafkaListenerInjector implements TraceInjector, ApplicationListener<ApplicationReadyEvent> {

    private final State state;

    @Autowired
    public KafkaListenerInjector(@Qualifier("defaultState") final State state) {
        this.state = state;
    }

    @Override
    public Function<ApplicationContext, Void> traceIdInjector() {
        return (applicationContext) -> {
            Map<String, ConsumerFactory> consumerFactoryMap = applicationContext.getBeansOfType(ConsumerFactory.class);
            if (consumerFactoryMap.size() > 0) {
                consumerFactoryMap.values().forEach(rt -> {
                    Map configurationProperties = rt.getConfigurationProperties();
                    configurationProperties.put("applicationContext", applicationContext);
                    configurationProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerTracer.class.getName());
                });
            } else {
                Map<String, ConcurrentKafkaListenerContainerFactory> concurrentKafkaListenerContainerFactoryMap = applicationContext.getBeansOfType(ConcurrentKafkaListenerContainerFactory.class);
                if (concurrentKafkaListenerContainerFactoryMap.size() > 0) {
                    concurrentKafkaListenerContainerFactoryMap.values().forEach(rt -> {
                        Map configurationProperties = rt.getConsumerFactory().getConfigurationProperties();
                        configurationProperties.put("applicationContext", applicationContext);
                        configurationProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerTracer.class.getName());
                    });
                }
            }
            return null;
        };
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        inject(applicationReadyEvent.getApplicationContext());
    }
}
