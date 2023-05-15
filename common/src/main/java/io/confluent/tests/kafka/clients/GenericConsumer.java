package io.confluent.tests.kafka.clients;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

public abstract class GenericConsumer<K,V> {

    final private Consumer<K,V> consumer;

    public GenericConsumer(Consumer<K,V> consumer) {
        this.consumer = consumer;
    }


    public GenericConsumer(Properties config) {
        defineDeserializers(config);
        defineGroupId(config);
        this.consumer = new KafkaConsumer<K, V>(config);      
    }

    public abstract void defineDeserializers(Properties properties);
    public abstract void defineGroupId(Properties properties);

    void startBySubscribing(List<String> topics) {
        consumer.subscribe(topics);
    }

    void startByAssigning(List<TopicPartition> topicPartitions) {
        consumer.assign(topicPartitions);
    }

    /**
     * Basic implementation for abstract consumer.
     * The implementation of this abstract class should implement the infinite poll loop
     * with exception handling in case of failures or consumer stop trigger.
     */
    public ConsumerRecords<K,V> read(long timeout) {
        return consumer.poll(Duration.of(timeout, ChronoUnit.MILLIS));
    }

    public void commit() {
        consumer.commitAsync();
    }

    public void stop() {
        consumer.wakeup();
    }

    public void close() {
        consumer.close();
    }
}
