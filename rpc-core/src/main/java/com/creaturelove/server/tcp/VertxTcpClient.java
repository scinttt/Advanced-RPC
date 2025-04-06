package com.creaturelove.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.creaturelove.RpcApplication;
import com.creaturelove.model.RpcRequest;
import com.creaturelove.model.RpcResponse;
import com.creaturelove.model.ServiceMetaInfo;
import com.creaturelove.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        // send TCP request
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    NetSocket socket = result.result();
                    // send data
                    // construct data
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    // generate global request ID
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // encode requests
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("Fail to encode protocol message");
                    }

                    // Receive responses
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (Exception e) {
                                    throw new RuntimeException("Fail to decode protocol message");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);

                });

        RpcResponse rpcResponse = responseFuture.get();
        // close the connection
        netClient.close();
        return rpcResponse;
    }
}
