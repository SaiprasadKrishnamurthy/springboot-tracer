package com.github.saiprasadkrishnamurthy.tracer.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "traceId")
public class TraceContext {
    private String traceId;
    private String traceTags;
    private Map<String, Object> metadata = new HashMap<>();
}
