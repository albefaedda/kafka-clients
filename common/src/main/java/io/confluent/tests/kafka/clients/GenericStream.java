package io.confluent.tests.kafka.clients;

import io.confluent.tests.kafka.clients.streams.topologies.StreamTopologyBuilder;
import org.apache.kafka.streams.KafkaStreams;

import java.util.Properties;

public abstract class GenericStream {

    private final StreamTopologyBuilder streamTopologyBuilder;
    private final Properties properties;

    public GenericStream(StreamTopologyBuilder builder, Properties configs) {
        this.streamTopologyBuilder = builder;
        this.properties = configs;
        defineSerdes(properties);
        defineAppId(properties);
    }

    public void runStream() {

        KafkaStreams streams = new KafkaStreams(streamTopologyBuilder.buildTopology(), properties);

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public abstract void defineSerdes(Properties properties);
    public abstract void defineAppId(Properties properties);

}
