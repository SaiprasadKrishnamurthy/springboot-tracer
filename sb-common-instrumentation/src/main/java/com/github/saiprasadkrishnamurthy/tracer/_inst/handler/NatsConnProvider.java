package com.github.saiprasadkrishnamurthy.tracer._inst.handler;

import com.github.saiprasadkrishnamurthy.tracer._inst.model.ThrowsConsumer;
import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class NatsConnProvider {

    private final String natsServerUrl;

    public NatsConnProvider(@Value("${nats.server.url}") final String natsServerUrl) {
        this.natsServerUrl = natsServerUrl;
    }

    public void doWithConnection(final ThrowsConsumer<Connection> connectionConsumer) {
        Connection connection = null;
        try {
            Options o = new Options.Builder().server(natsServerUrl).maxReconnects(-1).build();
            connection = Nats.connect(o);
            connectionConsumer.accept(connection);
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}
