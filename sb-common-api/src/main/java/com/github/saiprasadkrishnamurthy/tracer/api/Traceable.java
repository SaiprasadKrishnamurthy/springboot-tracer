package com.github.saiprasadkrishnamurthy.tracer.api;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Traceable {
    String description() default "";
}
