package com.creaturelove.server.tcp;

import com.creaturelove.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {
    @Override
    public void doStart(int port) {
        // Create Vert.x instance
        Vertx vertx = Vertx.vertx();

        // create TCP server
        NetServer server = vertx.createNetServer();

        // handle request
        server.connectHandler(new TcpServerHandler());

        // start the TCP server and listen the specific port
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port " + port);
            } else {
                log.info("Failed to start TCP server: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
