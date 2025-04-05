package com.creaturelove.server.tcp;

import io.vertx.core.Vertx;

public class VertxTcpClient {

    public void doStart(){
        // create Vert.x instance
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", result -> {
            if(result.succeeded()){
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();

                // send data
                socket.write("Hello, server!");

                // receive response
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            }else{
                System.err.println("Failed to connect to TCP server");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().doStart();
    }

}

