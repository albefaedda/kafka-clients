package io.confluent.tests.kafka.clients;

import io.confluent.tests.kafka.config.PropertiesFileLoader;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericConsumerTest {

    private static GenericConsumer<String,String> consumer;

    private static final String topic = "test-topic";

    private static Consumer<String,String> mockConsumer = new MockConsumer<String,String>(OffsetResetStrategy.EARLIEST);
    
    ClassLoader classLoader = getClass().getClassLoader();
    
    static PropertiesFileLoader cConfigs;

    public GenericConsumerTest() {
        File cConfigFile = new File(classLoader.getResource("consumer.properties").getFile());

        cConfigs = new PropertiesFileLoader(cConfigFile.getAbsolutePath());
    }

    @BeforeAll
    public static void init() throws IOException {

        consumer = new GenericConsumer<>(mockConsumer) {
            @Override
            public void defineDeserializers(Properties properties) {
                properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            }

            @Override
            public void defineGroupId(Properties properties) {
                properties.put(GROUP_ID_CONFIG, "my-consumer-test-group");
            }
        };
    }

    @Test
    public void testConsume() {
        int partition = 0;
        TopicPartition tp = new TopicPartition(topic, partition);

        // GIVEN
        ConsumerRecord<String,String> cr = new ConsumerRecord<String,String>(topic, partition, 0, "My String Key", "This is my value");
    
        consumer.startByAssigning(Arrays.asList(tp));

        ((MockConsumer<String,String>) mockConsumer).schedulePollTask(() -> ((MockConsumer<String,String>) mockConsumer).addRecord(cr));
        ((MockConsumer<String,String>) mockConsumer).schedulePollTask(() -> consumer.stop());

        // WHEN
        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        startOffsets.put(tp, 0L);
        ((MockConsumer<String,String>) mockConsumer).updateBeginningOffsets(startOffsets);

        // THEN
        ConsumerRecords<String, String> records = null;
        try{
            records = consumer.read(10L);
        } finally {
            consumer.close();
        }
        
        assertTrue(records.count() == 1, "Consumer shoudld only have received 1 message");

        ConsumerRecord<String, String> record = records.iterator().next();
        assertEquals("My String Key", record.key(), "The actual message Key should match 'My String Key'");
        assertEquals("This is my value", record.value(), "The actual message Value should match 'This is my value'");

        consumer.stop();
        assertTrue(((MockConsumer<String,String>) mockConsumer).closed());
    }
}