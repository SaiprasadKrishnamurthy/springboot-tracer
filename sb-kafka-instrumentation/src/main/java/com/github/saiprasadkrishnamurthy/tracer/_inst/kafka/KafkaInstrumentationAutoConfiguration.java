package com.github.saiprasadkrishnamurthy.tracer._inst.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "sb.instrumentation.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.github.saiprasadkrishnamurthy.tracer._inst.kafka")
public class KafkaInstrumentationAutoConfiguration {
}
