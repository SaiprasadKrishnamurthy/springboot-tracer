package com.github.saiprasadkrishnamurthy.tracer._inst.transformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saiprasadkrishnamurthy.tracer.api.MethodEvent;
import com.github.saiprasadkrishnamurthy.tracer.api.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer.api.RawEventTransformer;
import com.github.saiprasadkrishnamurthy.tracer.api.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.mapping.Document;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class MongoDBRawEventTransformer implements RawEventTransformer {
    @Override
    public boolean canHandle(final RawEvent rawEvent) {
        return rawEvent.getMethodInvocation().getThis().toString().contains("Mongo");
    }

    @Override
    public Optional<MethodEvent> transform(final RawEvent rawEvent) {
        log.info("Tracing MongoDB calls");
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
                    parseParams(rawEvent),
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

    private Map<String, Object> parseParams(final RawEvent rawEvent) {
        Method method = rawEvent.getMethodInvocation().getMethod();
        Map<String, Object> params = new HashMap<>();
        Arrays.asList(rawEvent.getMethodInvocation().getArguments()).stream().map(arg->arg.getClass().getName()).collect(Collectors.toList());
        params.put("parameterList", Arrays.asList(rawEvent.getMethodInvocation().getArguments()).stream().map(arg->arg.getClass().getName()).collect(Collectors.toList()));
        params.put("parameterCount", method.getParameterCount());
        params.put("parameterValues", Arrays.asList(rawEvent.getMethodInvocation().getArguments()).stream().map(arg-> {
            try {
                Optional<Annotation> traceable=Arrays.stream(arg.getClass().getAnnotations()).filter(annotation -> annotation.annotationType().getName().contains("Traceable")).findFirst();
                if(traceable.isPresent()) {
                    return new ObjectMapper().writeValueAsString(arg);
                }else{
                    return arg.getClass().getName();
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return arg.getClass().getName();
            }
        }
        ).collect(Collectors.toList()));
        return params;
    }

    private Map<String, Object> parseMetadata(TraceContext traceContext, final Method method) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("repositryType", "mongoDB");
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
