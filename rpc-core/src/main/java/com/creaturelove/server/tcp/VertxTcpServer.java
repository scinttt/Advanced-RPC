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
        server.connectHandler(socket -> {
            // construct parser
            RecordParser parser = RecordParser.newFixed(8);
            parser.setOutput(new Handler<Buffer>() {
                // initialization
                int size = -1;
                // get（header + body）
                Buffer resultBuffer = Buffer.buffer();

                @Override
                public void handle(Buffer buffer) {
                    if (-1 == size) {
                        // get message body length
                        size = buffer.getInt(4);
                        parser.fixedSizeMode(size);
                        // write header info to result
                        resultBuffer.appendBuffer(buffer);
                    } else {
                        // write body info to result
                        resultBuffer.appendBuffer(buffer);
                        System.out.println(resultBuffer.toString());
                        // do it again
                        parser.fixedSizeMode(8);
                        size = -1;
                        resultBuffer = Buffer.buffer();
                    }
                }
            });

            socket.handler(parser);
        });

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
