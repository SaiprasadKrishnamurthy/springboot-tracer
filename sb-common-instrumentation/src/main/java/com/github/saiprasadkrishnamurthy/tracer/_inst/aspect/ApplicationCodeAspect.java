package com.github.saiprasadkrishnamurthy.tracer._inst.aspect;

import com.github.saiprasadkrishnamurthy.tracer.api.State;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class ApplicationCodeAspect {

    private final State state;

    public ApplicationCodeAspect(@Qualifier("defaultState") final State state) {
        this.state = state;
    }

    @Bean
    public Advisor advisorBean(@Value("${instrumentation.base.package}") final String instrumentationBasePkg) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* " + instrumentationBasePkg + "..*.*(..)) && " +
                "!@annotation(org.springframework.context.annotation.Bean) && !@annotation(org.springframework.context.annotation.Configuration) && " +
                "!execution(* org.springframework..*.*(..)) && " +
                "!execution(* *.._inst..*.*(..))");
        return new DefaultPointcutAdvisor(pointcut, (MethodInterceptor) methodInvocation -> {
            Method method = methodInvocation.getMethod();
            Class<?> declaringClass = methodInvocation.getMethod().getDeclaringClass();
            System.out.println("[" + state.getTraceId() + "] ******** Beforeee *************** " + declaringClass.getSimpleName() + "::" + method.getName());
            Object result = methodInvocation.proceed();
            System.out.println("[" + state.getTraceId() + "] ******** Afterrr *************** " + declaringClass.getSimpleName() + "::" + method.getName());
            return result;
        });
    }
}
