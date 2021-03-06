package com.github.saiprasadkrishnamurthy.tracer._inst.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.tracer.api.MethodEvent;
import com.github.saiprasadkrishnamurthy.tracer.api.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer.api.RawEventTransformer;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Listener(references = References.Strong)
public class RawEventHandler {

    private final ApplicationContext applicationContext;

    public RawEventHandler(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Handler(delivery = Invoke.Asynchronously)
    public void handleRawEvent(final RawEvent rawEvent) {
        rawEvent.getApplicationContext().getBeansOfType(RawEventTransformer.class).values().stream()
                .filter(r -> r.canHandle(rawEvent))
                .map(r -> r.transform(rawEvent))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(MethodEvent::merge)
                .ifPresent(me -> {
                    NatsConnProvider natsConnProvider = rawEvent.getApplicationContext().getBean(NatsConnProvider.class);
                    String environmentPrefix = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.environment.prefix", "");
                    String tracingQueue = rawEvent.getApplicationContext().getEnvironment().getProperty("tracing.queue.method", "tracing_queue_method");
                    natsConnProvider.doWithConnection(conn -> conn.publish(environmentPrefix + "_" + tracingQueue, new ObjectMapper().writeValueAsBytes(me)));
                });
    }

    private Map<String, Object> parseMetadata(TraceContext traceContext, final Method method) {
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
            collect = Arrays.stream(interfaces)
                    .map(Class::getTypeParameters)
                    .filter(Objects::nonNull)
                    .flatMap(Arrays::stream)
                    .map(t -> t.getGenericDeclaration().getName())
                    .collect(toList());
            metadata.put("interfaceParameterTypes", collect);
        }
        TypeVariable<? extends Class<?>>[] typeParameters = method.getDeclaringClass().getTypeParameters();
        if (typeParameters != null) {
            collect = Arrays.stream(typeParameters)
                    .filter(Objects::nonNull)
                    .map(t -> t.getGenericDeclaration().getName())
                    .collect(toList());
            metadata.put("typeParameters", collect);
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
        metadata.putAll(traceContext.getMetadata());
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