package com.github.saiprasadkrishnamurthy.tracer._inst.provider.mdc;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceProvider;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Data
@Component
public class MdcTraceProvider implements TraceProvider {

    @Override
    public String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
}
