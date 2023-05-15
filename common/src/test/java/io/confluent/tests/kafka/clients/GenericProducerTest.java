package io.confluent.tests.kafka.clients;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.confluent.tests.kafka.config.PropertiesFileLoader;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenericProducerTest {
    
    private static GenericProducer<String,String> producer;
    private static final String topic = "test-topic";

    private static Producer<String, String> mockProducer = new MockProducer<String, String>(true, new StringSerializer(), new StringSerializer());

    ClassLoader classLoader = getClass().getClassLoader();
    
    static PropertiesFileLoader pConfigs;

    public GenericProducerTest() {
        File pConfigFile = new File(classLoader.getResource("producer.properties").getFile());

        pConfigs = new PropertiesFileLoader(pConfigFile.getAbsolutePath());
    }

    @BeforeAll
    public static void init() throws IOException {
    
        producer = new GenericProducer<>(mockProducer, topic) {
            @Override
            public void defineSerializers(Properties properties) {
                properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                properties.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            }
        };
    }

    @Test
    public void testProduce() {
        producer.send("My First Key", "My First Value");
        int msgsSent = ((MockProducer<String,String>)mockProducer).history().size();
        assertTrue(msgsSent == 1, "The producer should've sent 1 message");
        assertTrue(((MockProducer<String,String>)mockProducer).history().get(0).key().equalsIgnoreCase("My First Key"));
        assertTrue(((MockProducer<String,String>)mockProducer).history().get(0).value().equalsIgnoreCase("My First Value"));
        producer.close();
    }
}
