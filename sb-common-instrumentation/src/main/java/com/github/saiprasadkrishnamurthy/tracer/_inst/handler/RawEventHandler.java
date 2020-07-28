package com.github.saiprasadkrishnamurthy.tracer._inst.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.TraceEvent;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Listener(references = References.Strong)
public class RawEventHandler {

    @Handler(delivery = Invoke.Asynchronously)
    public void handleEvent(final RawEvent rawEvent) {
        try {
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
            // TODO Check if this doesn't leak? Shouldn't we close?
            Connection natsConn = rawEvent.getApplicationContext().getBean(Connection.class);
            String environmentPrefix = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.environment.prefix", "");
            String tracingQueue = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.queue", "tracing_queue");
            natsConn.publish(environmentPrefix + "_" + tracingQueue, new ObjectMapper().writeValueAsBytes(traceEvent));
        } catch (Exception ex) {
            log.error("Can't publish trace event for traceId: " + rawEvent.getTraceContext().getTraceId(), ex);
        }
    }

    private Map<String, Object> parseParams(final Method method) {
        // TODO
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