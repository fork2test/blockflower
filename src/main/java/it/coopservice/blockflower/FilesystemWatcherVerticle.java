package it.coopservice.blockflower;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.time.Instant;
import java.util.UUID;

public class FilesystemWatcherVerticle extends AbstractVerticle {

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
        mongoClient = MongoClient.createShared(vertx, config());
        vertx.setTimer(10 * 1000, timer -> {
            vertx.fileSystem().readDir(to_work, files -> {
                if (files.succeeded()) {
                    files.result().forEach(single -> {
                        // mv file
                        vertx.fileSystem().move(to_work + single, working + single, res -> {
                            if (res.succeeded()) {
                                // registro il file su mongoDB
                                JsonObject document = new JsonObject().put("filename", single)
                                        .put("_id", UUID.randomUUID().toString())
                                        .put("init", Instant.now());
                                mongoClient.save("files", document, saved -> {
                                    if (saved.succeeded()) {
                                        vertx.eventBus().send("files", document);
                                    } else {
                                        System.out.println("error in saving on mongo single file:" + single);

                                    }
                                });

                            } else {
                                System.out.println("error in moving single file:" + single);
                            }
                        });
                    });
                } else {
                    System.out.println("no files");
                }
            });
        });
    }

    @Override
    public void stop() {

    }

}
