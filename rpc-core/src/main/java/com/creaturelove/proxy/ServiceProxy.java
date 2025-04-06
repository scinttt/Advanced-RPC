package com.creaturelove.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.creaturelove.RpcApplication;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.constant.RpcConstant;
import com.creaturelove.model.RpcRequest;
import com.creaturelove.model.RpcResponse;
import com.creaturelove.model.ServiceMetaInfo;
import com.creaturelove.protocol.*;
import com.creaturelove.registry.Registry;
import com.creaturelove.registry.RegistryFactory;
import com.creaturelove.serializer.JdkSerializer;
import com.creaturelove.serializer.Serializer;
import com.creaturelove.serializer.SerializerFactory;
import com.creaturelove.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.Provider;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
         // final Serializer serializer = new JdkSerializer();

        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // Construct request
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try{
            // get provider address from registry center
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("service address temporarily doesn't exist");
            }

            // get first one
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // send TCP request
            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();


              // http protocol
//            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bytes)
//                    .execute()
//            ){
//                byte[] result = httpResponse.bodyBytes();
//
//                // deserialization
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}

