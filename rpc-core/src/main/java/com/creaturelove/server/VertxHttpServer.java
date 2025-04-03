package com.creaturelove.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{
    public void doStart(int port){
        Vertx vertx = Vertx.vertx();

        // create http server
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        server.requestHandler(new HttpServerHandler());

        // Start Http Server and Listen the specific port
        server.listen(port, result -> {
            if(result.succeeded()){
                System.out.println("Server is now listening on port: " + port);
            }else{
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}

