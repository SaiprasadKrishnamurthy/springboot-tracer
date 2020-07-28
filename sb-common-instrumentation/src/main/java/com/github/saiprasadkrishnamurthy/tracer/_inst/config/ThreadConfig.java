package com.github.saiprasadkrishnamurthy.tracer._inst.config;

import com.github.saiprasadkrishnamurthy.tracer._inst.handler.RawEventHandler;
import net.engio.mbassy.bus.MBassador;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class ThreadConfig {

    @Bean("_executorService")
    public ContextAwareExecutorService _executorService() {
        return new ContextAwareExecutorService<>(ForkJoinPool.commonPool());
    }

    @Bean
    public MBassador mBassador() {
        MBassador mBassador = new MBassador();
        mBassador.subscribe(new RawEventHandler());
        return mBassador;
    }
}
