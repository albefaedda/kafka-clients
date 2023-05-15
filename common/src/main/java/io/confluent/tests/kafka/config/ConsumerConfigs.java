package io.confluent.tests.kafka.config;

import java.io.IOException;
import java.util.Properties;

public class ConsumerConfigs implements FileLoader {

    private final String fileName;

    public ConsumerConfigs(String fileName) {
        this.fileName = fileName;
    }

    public Properties getConfig() throws IOException {
        return load(fileName);
    }
}
