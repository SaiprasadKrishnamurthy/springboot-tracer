package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.util;

import com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector.ConsumerTracer;
import com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector.ProducerTracer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class KafkaTracingConfigInjector {

    private static Boolean PRODUCER_CONFIG_SET = false;
    private static Boolean CONSUMER_CONFIG_SET = false;


    public static Map addProducerTracingConfigs(Map kafkaConfigs, ApplicationContext applicationContext) {
        kafkaConfigs.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerTracer.class.getName());
        kafkaConfigs.put("applicationContext", applicationContext);
        PRODUCER_CONFIG_SET = true;
        return kafkaConfigs;
    }

    public static Map addConsumerTracingConfigs(Map kafkaConfigs, ApplicationContext applicationContext) {
        kafkaConfigs.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, ConsumerTracer.class.getName());
        kafkaConfigs.put("applicationContext", applicationContext);
        CONSUMER_CONFIG_SET = true;
        return kafkaConfigs;
    }

    public static boolean isProducerConfigSet() {
        return PRODUCER_CONFIG_SET;
    }

    public static boolean isConsumerConfigSet() {
        return CONSUMER_CONFIG_SET;
    }


}


