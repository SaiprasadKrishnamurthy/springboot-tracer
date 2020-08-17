package com.github.saiprasadkrishnamurthy.tracer._inst.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.tracer._inst.handler.NatsConnProvider;
import com.github.saiprasadkrishnamurthy.tracer.api.MethodEvent;
import com.github.saiprasadkrishnamurthy.tracer.api.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer.api.RawEventTransformer;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class DefaultRawEventTransformer implements RawEventTransformer {
    @Override
    public boolean canHandle(final RawEvent rawEvent) {
        return true;
    }

    @Override
    public Optional<MethodEvent> transform(final RawEvent rawEvent) {
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
                    parseMetadata(rawEvent.getTraceContext(), method),
                    rawEvent.getTags() != null ? new LinkedHashSet<>(rawEvent.getTags()) : new LinkedHashSet<>(),
                    rawEvent.getThreadId(),
                    rawEvent.getTimeTakenInMillis());
            return Optional.of(traceEvent);
        } catch (Exception ex) {
            log.error("Can't publish trace event for traceId: " + rawEvent.getTraceContext().getTraceId(), ex);
        }
        return Optional.empty();
    }

    private Map<String, Object> parseParams(final Method method) {
        // TODO
        return Collections.emptyMap();
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

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "NA";
        }
    }
}
