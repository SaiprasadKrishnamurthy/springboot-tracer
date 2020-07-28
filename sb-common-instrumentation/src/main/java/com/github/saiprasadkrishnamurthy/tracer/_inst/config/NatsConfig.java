package com.github.saiprasadkrishnamurthy.tracer._inst.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NatsConfig {

    @Bean(value = "natsConnection", destroyMethod = "close")
    public Connection natsConnection(@Value("${nats.server.url}") final String natsServerUrl) throws Exception {
        Options o = new Options.Builder().server(natsServerUrl).maxReconnects(-1).build();
        return Nats.connect(o);
    }
}
