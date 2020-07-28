package com.github.saiprasadkrishnamurthy.tracer._inst.aspect;

import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEvent;
import com.github.saiprasadkrishnamurthy.tracer._inst.model.TraceEventType;
import com.github.saiprasadkrishnamurthy.tracer.api.State;
import net.engio.mbassy.bus.MBassador;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ApplicationCodeAspect {

    private final State state;
    private final MBassador mBassador;
    private String appName;

    public ApplicationCodeAspect(@Qualifier("defaultState") final State state, final MBassador mBassador, @Value("${spring.application.name}") final String appName) {
        this.state = state;
        this.mBassador = mBassador;
        this.appName = appName;
    }

    @Bean
    public Advisor advisorBean(@Value("${instrumentation.base.package}") final String instrumentationBasePkg) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* " + instrumentationBasePkg + "..*.*(..)) && " +
                "!@annotation(org.springframework.context.annotation.Bean) && !@annotation(org.springframework.context.annotation.Configuration) && " +
                "!execution(* org.springframework..*.*(..)) && " +
                "!execution(* *.._inst..*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, (MethodInterceptor) methodInvocation -> {
            Object result;
            mBassador.publish(new RawEvent(state.getTraceId(), appName, methodInvocation, TraceEventType.Entry, null, System.currentTimeMillis(), null, Thread.currentThread().getName()));
            try {
                result = methodInvocation.proceed();
                mBassador.publish(new RawEvent(state.getTraceId(), appName, methodInvocation, TraceEventType.Exit, null, System.currentTimeMillis(), null, Thread.currentThread().getName()));
            } catch (Throwable err) {
                mBassador.publish(new RawEvent(state.getTraceId(), appName, methodInvocation, TraceEventType.Error, err, System.currentTimeMillis(), null, Thread.currentThread().getName()));
                throw err;
            }
            return result;
        });
    }
}
