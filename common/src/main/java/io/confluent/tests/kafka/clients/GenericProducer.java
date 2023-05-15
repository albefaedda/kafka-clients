package io.confluent.tests.kafka.clients;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public abstract class GenericProducer<K,V> {

    final private String topic;
    final private Producer<K,V> producer;

    public GenericProducer(Producer<K,V> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    public GenericProducer(String topic, Properties config) {
        this.topic = topic;
        defineSerializers(config);
        this.producer = new KafkaProducer<>(config);
    }

    public abstract void defineSerializers(Properties properties);

    public void send(K key, V value) {
        ProducerRecord<K,V> record = new ProducerRecord<>(topic, key, value);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Exception occurred while sending message(s): " + exception.getMessage());
            } else {
                System.out.println("Message Written to topic " + metadata.topic() + " with offset: " + metadata.offset());
            }
        });
    }

    public void close() {
        producer.flush();
        producer.close();
    }
}
