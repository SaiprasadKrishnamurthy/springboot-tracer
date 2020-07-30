package com.github.saiprasadkrishnamurthy.tracer._inst.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.MethodEvent;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEventWhole;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.TraceEvent;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static java.util.stream.Collectors.toList;

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
                    rawEvent.getThreadId(),
                    rawEvent.getTimeTakenInMillis());
            NatsConnProvider natsConnProvider = rawEvent.getApplicationContext().getBean(NatsConnProvider.class);
            String environmentPrefix = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.environment.prefix", "");
            String tracingQueue = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.queue", "tracing_queue");
            natsConnProvider.doWithConnection(natsConn -> natsConn.publish(environmentPrefix + "_" + tracingQueue, new ObjectMapper().writeValueAsBytes(traceEvent)));
        } catch (Exception ex) {
            log.error("Can't publish trace event for traceId: " + rawEvent.getTraceContext().getTraceId(), ex);
        }
    }

    @Handler(delivery = Invoke.Asynchronously)
    public void handleEventWhole(final RawEventWhole rawEvent) {
        try {
            Method method = rawEvent.getMethodInvocation().getMethod();
            Class<?> declaringClass = rawEvent.getMethodInvocation().getMethod().getDeclaringClass();
            MethodEvent traceEvent = new MethodEvent(rawEvent.getTraceContext(),
                    rawEvent.getAppName(),
                    declaringClass.getName(),
                    method.getName(),
                    rawEvent.getStart(),
                    rawEvent.getEnd(),
                    getHostName(),
                    parseParams(method),
                    parseMetadata(method),
                    rawEvent.getTags(),
                    rawEvent.getThreadId(),
                    rawEvent.getTimeTakenInMillis());
            NatsConnProvider natsConnProvider = rawEvent.getApplicationContext().getBean(NatsConnProvider.class);
            String environmentPrefix = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.environment.prefix", "");
            String tracingQueue = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.queue.whole", "tracing_queue_whole");
            natsConnProvider.doWithConnection(conn -> conn.publish(environmentPrefix + "_" + tracingQueue, new ObjectMapper().writeValueAsBytes(traceEvent)));
        } catch (Exception ex) {
            log.error("Can't publish trace event for traceId: " + rawEvent.getTraceContext().getTraceId(), ex);
        }
    }

    private Map<String, Object> parseMetadata(final Method method) {
        Map<String, Object> metadata = new HashMap<>();
        List<String> collect = Arrays.stream(method.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .map(Class::getName)
                .collect(toList());
        metadata.put("methodAnnotations", collect);
        Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
        if (interfaces != null) {
            collect = Arrays.stream(interfaces).map(Class::getSimpleName).collect(toList());
            metadata.put("implementingInterfaces", collect);
        }
        Class<?> superClass = method.getDeclaringClass().getSuperclass();
        if (superClass != null) {
            metadata.put("superClass", superClass.getName());
        }
        Annotation[] classAnnotations = method.getDeclaringClass().getDeclaredAnnotations();
        if (classAnnotations != null) {
            metadata.put("classAnnotations", Arrays.stream(classAnnotations).map(Annotation::annotationType).collect(toList()));
        }
        if (method.getDeclaringClass().getSuperclass() != null) {
            classAnnotations = method.getDeclaringClass().getSuperclass().getDeclaredAnnotations();
            if (classAnnotations != null) {
                metadata.put("superClassAnnotations", Arrays.stream(classAnnotations).map(Annotation::annotationType).collect(toList()));
            }
        }
        return metadata;
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