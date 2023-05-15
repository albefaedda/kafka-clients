package io.confluent.tests.kafka.clients.streams.topologies;

import org.apache.kafka.streams.Topology;

public abstract class StreamTopologyBuilder {

    protected final String inputTopic;
    protected final String outputTopic;

    public StreamTopologyBuilder(String inputTopic, String outputTopic) {
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    public abstract Topology buildTopology();

}
