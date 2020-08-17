package com.github.saiprasadkrishnamurthy.tracer.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodEvent {
    private TraceContext traceContext;
    private String appName;
    private String className;
    private String methodName;
    private long start;
    private long end;
    private String host;
    private Map<String, Object> params = new LinkedHashMap<>();
    private Map<String, Object> metadata = new LinkedHashMap<>();
    private Set<String> tags = new LinkedHashSet<>();
    private String threadId;
    private long timeTakenInMillis;

    public MethodEvent merge(final MethodEvent methodEvent) {
        this.params.putAll(methodEvent.getParams());
        this.metadata.putAll(methodEvent.getMetadata());
        this.tags.addAll(methodEvent.getTags());
        return this;
    }
}
