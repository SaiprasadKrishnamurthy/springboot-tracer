package com.github.saiprasadkrishnamurthy.tracer._inst.provider.http;

import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
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
    public TraceContext getTraceContext() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attrs = ((ServletRequestAttributes) attribs);
        if (attrs != null) {
            String id = attrs.getRequest().getHeader(TRACE_ID_KEY);
            String tags = attrs.getRequest().getHeader(TRACE_TAGS_KEY);
            return new TraceContext(id, tags);
        }
        return null;
    }
}
