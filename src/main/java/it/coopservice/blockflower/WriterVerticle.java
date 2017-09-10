package it.coopservice.blockflower;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.util.HashMap;
import java.util.Map;

public class WriterVerticle extends AbstractVerticle {


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new WriterVerticle());
    }


    @Override
    public void start() throws Exception {

        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("zookeeper.host" , "localhost:2181");
        config.put("group.id", "default");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");

// use producer for interacting with Apache Kafka
        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);

        for (int i = 0; i < 5; i++) {
            System.out.println("send " + i);
            // only topic and message value are specified, round robin on destination partitions
            KafkaProducerRecord<String, String> record =
                    KafkaProducerRecord.create("test", "message_" + i);

            producer.write(record);
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop");
    }

}
