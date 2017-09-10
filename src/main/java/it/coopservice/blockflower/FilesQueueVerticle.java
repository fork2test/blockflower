package it.coopservice.blockflower;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.time.Instant;

public class FilesQueueVerticle extends AbstractVerticle {

    final String to_work = "csv/to_work/";
    final String working = "csv/working/";
    final String worked = "csv/worked/";

    MongoClient mongoClient;


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FilesystemWatcherVerticle());
    }


    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("files", file -> {
            System.out.println("new msg arrived in queue 'files'");
            JsonObject jsonObject = (JsonObject) file.body();

            // splitto il file in tanti piccoli json object da mandare in kafka
            
            JsonObject update = new JsonObject()
                    .put("$set", new JsonObject()
                            .put("parsing_date", Instant.now())
                            .put("records", Long.valueOf(33)));
            mongoClient.update("files", jsonObject, update, res -> {

                if (res.succeeded()) {

                    System.out.println("Book updated !");

                } else {

                    res.cause().printStackTrace();
                }

            });
        });
    }

    @Override
    public void stop() throws Exception {

    }
}
