package com.github.saiprasadkrishnamurthy.tracer._inst.handler;

import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.TraceEvent;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

@Listener(references = References.Strong)
public class RawEventHandler {

    @Handler(delivery = Invoke.Asynchronously)
    public void expensiveOperation(final RawEvent rawEvent) {
        Method method = rawEvent.getMethodInvocation().getMethod();
        Class<?> declaringClass = rawEvent.getMethodInvocation().getMethod().getDeclaringClass();
        TraceEvent traceEvent = new TraceEvent(rawEvent.getTraceContext(),
                rawEvent.getAppName(),
                declaringClass.getName(),
                method.getName(),
                rawEvent.getTimestamp(),
                rawEvent.getTraceEventType(),
                getHostName(),
                parseParams(method),
                rawEvent.getTags(),
                rawEvent.getThreadId());
        System.out.println(traceEvent);
    }

    private Map<String, Object> parseParams(final Method method) {
        return Collections.emptyMap();
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "NA";
        }
    }
}