package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.util;

import com.github.saiprasadkrishnamurthy.tracer._inst.kafka.exception.RequiredPropertyMissingException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConfigsChecker implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (!KafkaTracingConfigInjector.isConsumerConfigSet()) {
            throw new RequiredPropertyMissingException("Application Context and Consumer Interceptor is not been set. Please invoke" +
                    "KafkaTracingConfigInjector.addConsumerTracingConfigs by passing your kafka consumer configuration");
        }
        if (!KafkaTracingConfigInjector.isProducerConfigSet()) {
            throw new RequiredPropertyMissingException("Application Context and Producer Interceptor is not been set. Please invoke" +
                    "KafkaTracingConfigInjector.addProducerTracingConfigs by passing your kafka producer configuration");
        }
    }
}
