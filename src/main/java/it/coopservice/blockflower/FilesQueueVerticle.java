package it.coopservice.blockflower;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

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
        });
    }

    @Override
    public void stop() throws Exception {

    }
}
