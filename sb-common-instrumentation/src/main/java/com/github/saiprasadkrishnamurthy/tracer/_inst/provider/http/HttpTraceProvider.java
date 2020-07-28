package com.github.saiprasadkrishnamurthy.tracer._inst.provider.http;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceProvider;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Data
@Component
public class HttpTraceProvider implements TraceProvider {

    @Override
    public String getTraceId() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attrs = ((ServletRequestAttributes) attribs);
        if (attrs != null) {
            return attrs.getRequest().getHeader(TRACE_ID_KEY);
        }
        return null;
    }
}
