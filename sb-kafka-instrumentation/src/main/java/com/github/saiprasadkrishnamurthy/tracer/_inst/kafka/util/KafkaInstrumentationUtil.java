package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.util;

import com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector.ConsumerTracer;
import com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector.ProducerTracer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class KafkaInstrumentationUtil {

    private static Boolean PRODUCER_CONFIG_SET = false;
    private static Boolean CONSUMER_CONFIG_SET = false;


    public static Map instrumentProducer(Map kafkaProducerConfigs, ApplicationContext applicationContext) {
        kafkaProducerConfigs.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerTracer.class.getName());
        kafkaProducerConfigs.put("applicationContext", applicationContext);
        PRODUCER_CONFIG_SET = true;
        return kafkaProducerConfigs;
    }

    public static Map instrumentConsumer(Map kafkaConsumerConfigs, ApplicationContext applicationContext) {
        kafkaConsumerConfigs.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, ConsumerTracer.class.getName());
        kafkaConsumerConfigs.put("applicationContext", applicationContext);
        CONSUMER_CONFIG_SET = true;
        return kafkaConsumerConfigs;
    }

    public static boolean isProducerConfigSet() {
        return PRODUCER_CONFIG_SET;
    }

    public static boolean isConsumerConfigSet() {
        return CONSUMER_CONFIG_SET;
    }


}


