package com.github.saiprasadkrishnamurthy.tracer._inst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class ThreadConfig {

    @Bean("_executorService")
    public ContextAwareExecutorService _executorService() {
        return new ContextAwareExecutorService<>(ForkJoinPool.commonPool());
    }
}
