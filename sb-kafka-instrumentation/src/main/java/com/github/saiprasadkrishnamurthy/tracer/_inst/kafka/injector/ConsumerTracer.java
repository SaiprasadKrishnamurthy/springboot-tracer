package com.github.saiprasadkrishnamurthy.tracer._inst.kafka.injector;

import com.github.saiprasadkrishnamurthy.tracer._inst.provider.DefaultState;
import com.github.saiprasadkrishnamurthy.tracer.api.State;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.saiprasadkrishnamurthy.tracer.api.TraceConstants.TRACE_ID_KEY;
import static com.github.saiprasadkrishnamurthy.tracer.api.TraceConstants.TRACE_TAGS_KEY;

public class ConsumerTracer<K, V> implements ConsumerInterceptor<K, V> {

    private ApplicationContext applicationContext;

    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> consumerRecords) {
        State state = applicationContext.getBean("defaultState", DefaultState.class);
        consumerRecords.forEach(record -> {
            AtomicReference<String> traceId = new AtomicReference<>();
            AtomicReference<String> traceTags = new AtomicReference<>();
            record.headers().forEach(header -> {
                if (header.key().equals(TRACE_ID_KEY)) {
                    traceId.set(new String(header.value()));
                }
                if (header.key().equals(TRACE_TAGS_KEY) && header.value() != null) {
                    traceTags.set(new String(header.value()));
                }
            });
            if (traceId.get() != null) {
                HashMap<String, Object> metadata = new HashMap<>();
                metadata.put("kafkaTopic", record.topic());
                state.propagate(new TraceContext(traceId.get(), traceTags.get(), metadata));
            }
        });
        return consumerRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
        this.applicationContext = (ApplicationContext) map.get("applicationContext");
    }
}
