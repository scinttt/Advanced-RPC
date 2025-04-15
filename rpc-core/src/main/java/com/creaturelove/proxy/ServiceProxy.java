package com.creaturelove.proxy;

import cn.hutool.core.collection.CollUtil;

import com.creaturelove.RpcApplication;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.constant.RpcConstant;
import com.creaturelove.fault.retry.RetryStrategy;
import com.creaturelove.fault.retry.RetryStrategyFactory;
import com.creaturelove.loadbalancer.LoadBalancer;
import com.creaturelove.loadbalancer.LoadBalancerFactory;
import com.creaturelove.model.RpcRequest;
import com.creaturelove.model.RpcResponse;
import com.creaturelove.model.ServiceMetaInfo;
import com.creaturelove.registry.Registry;
import com.creaturelove.registry.RegistryFactory;
import com.creaturelove.server.tcp.VertxTcpClient;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
         // final Serializer serializer = new JdkSerializer();

        // final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

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

            // Load Balancing
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());

            // use method name as load balancing parameter
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // rpc request with retry mechanism
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
//            // rpc request without retry mechanism using tcp protocol
//            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);

            return rpcResponse.getData();


//            // get first one
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
//
//            // send TCP request
//            RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
//            return rpcResponse.getData();


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
            throw new RuntimeException("Call failed");
        }
    }
}

