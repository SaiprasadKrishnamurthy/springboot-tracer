package com.github.saiprasadkrishnamurthy.tracer._inst.aspect;

import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEventWhole;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.TraceEventType;
import com.github.saiprasadkrishnamurthy.tracer.api.State;
import net.engio.mbassy.bus.MBassador;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Component
public class ApplicationCodeAspect {

    private final State state;
    private final MBassador mBassador;
    private String appName;
    private final ApplicationContext applicationContext;

    public ApplicationCodeAspect(@Qualifier("defaultState") final State state, final MBassador mBassador, @Value("${spring.application.name}") final String appName,
                                 final ApplicationContext applicationContext) {
        this.state = state;
        this.mBassador = mBassador;
        this.appName = appName;
        this.applicationContext = applicationContext;
    }

    @Bean
    public Advisor advisorBean(@Value("${instrumentation.base.package}") final String instrumentationBasePkg,
                               @Value("${instrumentation.components:standard}") final String components) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        String expressions = "execution(* " + instrumentationBasePkg + "..*.*(..)) && " +
                "!@annotation(org.springframework.context.annotation.Bean) && !@annotation(org.springframework.context.annotation.Configuration) && " +
                "!execution(* org.springframework..*.*(..)) && " +
                "!execution(* *.._inst..*.*(..))";
        List<String> coms = Arrays.stream(components.split(",")).map(String::toLowerCase).collect(Collectors.toList());
        if (coms.contains("mongo") || coms.contains("mongodb")) {
            expressions += " || target(org.springframework.data.mongodb.repository.MongoRepository) && execution(* org.springframework.data.repository.CrudRepository.*(..))";
        }
        pointcut.setExpression(expressions);
        return new DefaultPointcutAdvisor(pointcut, (MethodInterceptor) methodInvocation -> {
            long start = System.currentTimeMillis();
            Object result;
            mBassador.publish(new RawEvent(state.getTraceContext(), appName, methodInvocation, TraceEventType.Entry, null, System.currentTimeMillis(), null, Thread.currentThread().getName(), applicationContext, -1));
            try {
                result = methodInvocation.proceed();
                long end = System.currentTimeMillis();
                mBassador.publish(new RawEvent(state.getTraceContext(), appName, methodInvocation, TraceEventType.Exit, null, System.currentTimeMillis(), null, Thread.currentThread().getName(), applicationContext, end - start));
                mBassador.publish(new RawEventWhole(state.getTraceContext(), appName, methodInvocation, TraceEventType.Exit, null, start, end, null, Thread.currentThread().getName(), applicationContext, end - start));
            } catch (Throwable err) {
                mBassador.publish(new RawEvent(state.getTraceContext(), appName, methodInvocation, TraceEventType.Error, err, System.currentTimeMillis(), null, Thread.currentThread().getName(), applicationContext, System.currentTimeMillis() - start));
                mBassador.publish(new RawEventWhole(state.getTraceContext(), appName, methodInvocation, TraceEventType.Exit, null, start, System.currentTimeMillis(), null, Thread.currentThread().getName(), applicationContext, System.currentTimeMillis() - start));
                throw err;
            }
            return result;
        });
    }

    @Bean
    public Advisor advisorBean1(@Value("${instrumentation.base.package}") final String instrumentationBasePkg,
                                @Value("${instrumentation.components:standard}") final String components) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        String expressions = "execution(*  " +
                "!@annotation(org.springframework.context.annotation.Bean) && !@annotation(org.springframework.context.annotation.Configuration) && " +
                "!execution(* org.springframework..*.*(..)) && " +
                "!execution(* *.._inst..*.*(..))";
        List<String> coms = Arrays.stream(components.split(",")).map(String::toLowerCase).collect(Collectors.toList());
        if (coms.contains("mongo") || coms.contains("mongodb")) {
            expressions += " || target(org.springframework.data.mongodb.repository.MongoRepository) && execution(* org.springframework.data.repository.CrudRepository.*(..))";
        }
        pointcut.setExpression(expressions);
        return new DefaultPointcutAdvisor(pointcut, (MethodInterceptor) methodInvocation -> {
            System.out.println("___________________****______________");
            return methodInvocation.proceed();
        });
    }
}
