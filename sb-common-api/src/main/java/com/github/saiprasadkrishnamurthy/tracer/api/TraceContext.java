package com.github.saiprasadkrishnamurthy.tracer.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "traceId")
public class TraceContext {
    private String traceId;
    private String traceTags;
}
