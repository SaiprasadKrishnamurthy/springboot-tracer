package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContextSupplier;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class ProducerTracer<K, V> implements ProducerInterceptor<K, V>, TraceContextSupplier {

    private ApplicationContext applicationContext;

    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> producerRecord) {
        TraceContext traceContext = traceContextSupplier(applicationContext).get();
        Map<String, Object> metadata = traceContext.getMetadata();
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put("kafkaTopic", producerRecord.topic());
        producerRecord.headers()
                .add(TRACE_ID_KEY, traceContext.getTraceId().getBytes())
                .add(TRACE_TAGS_KEY, traceContext.getTraceTags() != null ? traceContext.getTraceTags().getBytes() : null);
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
        this.applicationContext = (ApplicationContext) map.get("applicationContext");
    }
}
