package io.confluent.tests.kafka.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ConfigFileLoaderTest {
    
    @Test
    public void loadProperties() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File pConfigFile = new File(classLoader.getResource("producer.properties").getFile());
        File cConfigFile = new File(classLoader.getResource("consumer.properties").getFile());

        PropertiesFileLoader pConfigs = new PropertiesFileLoader(pConfigFile.getAbsolutePath());
        PropertiesFileLoader cConfigs = new PropertiesFileLoader(cConfigFile.getAbsolutePath());

        assertNotNull(pConfigs.getConfig(), "Producer config should be loaded correctly");
        assertNotNull(cConfigs.getConfig(), "Consumer config should be loaded correctly");
    }
}
