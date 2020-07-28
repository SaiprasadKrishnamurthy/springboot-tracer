package com.github.saiprasadkrishnamurthy.tracer._inst.model;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceEventWhole {
    private TraceContext traceContext;
    private String appName;
    private String className;
    private String methodName;
    private long start;
    private long end;
    private String host;
    private Map<String, Object> params = new LinkedHashMap<>();
    private List<String> tags = new ArrayList<>();
    private String threadId;
    private long timeTakenInMillis;
}
