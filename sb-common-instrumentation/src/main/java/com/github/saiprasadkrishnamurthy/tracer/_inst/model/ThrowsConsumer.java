package com.github.saiprasadkrishnamurthy.tracer._inst.model;

@FunctionalInterface
public interface ThrowsConsumer<T> {
    void accept(T t) throws Exception;
}
