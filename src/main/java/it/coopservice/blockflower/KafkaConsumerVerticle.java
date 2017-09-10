package it.coopservice.blockflower;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KafkaConsumerVerticle extends AbstractVerticle {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new KafkaConsumerVerticle());
    }


    @Override
    public void start() throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("zookeeper.host" , "localhost:2181");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "default");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "false");
        System.out.println("connettiamo");
// use consumer for interacting with Apache Kafka
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
        consumer.handler(record -> {
            System.out.println("Processing key=" + record.key() + ",value=" + record.value() +
                    ",partition=" + record.partition() + ",offset=" + record.offset());
        });

// subscribe to several topics
        Set<String> topics = new HashSet<>();
        topics.add("test");
        consumer.subscribe(topics);

// or just subscribe to a single topic
    }


    @Override
    public void stop() throws Exception {
        System.out.println("stop");
    }
}
