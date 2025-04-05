package com.creaturelove.server.tcp;

import com.creaturelove.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData) {
        // request logic
        return "Hello, clinet".getBytes();
    }

    @Override
    public void doStart(int port) {
        // create vert.x instance
        Vertx vertx = Vertx.vertx();

        // create tcp server
        NetServer server = vertx.createNetServer();

        // handle request
        server.connectHandler(socket -> {
            // handle connection
            socket.handler(buffer -> {
                // handle byte[]
                byte[] requestData = buffer.getBytes();
                // customize byte[] handle logic
                byte[] responseData = handleRequest(requestData);
                // send response
                socket.write(Buffer.buffer(requestData));
            });
        });

        // start TCP server and listen the specific port
        server.listen(port, result -> {
            if(result.succeeded()){
                System.out.println("TCP sever started on port: " + port);
            }else{
                System.out.println("Failed to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}