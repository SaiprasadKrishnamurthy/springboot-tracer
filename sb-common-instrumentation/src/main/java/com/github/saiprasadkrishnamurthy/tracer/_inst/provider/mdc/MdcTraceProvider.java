package com.github.saiprasadkrishnamurthy.tracer._inst.provider.mdc;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceProvider;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Data
@Component
public class MdcTraceProvider implements TraceProvider {

    @Override
    public TraceContext getTraceContext() {
        String id = MDC.get(TRACE_ID_KEY);
        String tags = MDC.get(TRACE_TAGS_KEY);
        return new TraceContext(id, tags, new HashMap<>());
    }
}
