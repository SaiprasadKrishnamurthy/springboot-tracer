package com.github.saiprasadkrishnamurthy.tracer._inst.handler;

import com.github.saiprasadkrishnamurthy.tracer._inst.model.RawEvent;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import java.lang.reflect.Method;

@Listener(references = References.Strong)
public class RawEventHandler {

    @Handler(delivery = Invoke.Asynchronously)
    public void expensiveOperation(final RawEvent rawEvent) {
        Method method = rawEvent.getMethodInvocation().getMethod();
        Class<?> declaringClass = rawEvent.getMethodInvocation().getMethod().getDeclaringClass();
        System.out.println(rawEvent.getTraceEventType() + " --- " + declaringClass.getSimpleName() + "::" + method.getName());
    }
}