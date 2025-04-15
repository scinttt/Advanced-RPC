package com.creaturelove.bootstrap;

import com.creaturelove.RpcApplication;
import com.creaturelove.config.RegistryConfig;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.model.ServiceMetaInfo;
import com.creaturelove.model.ServiceRegisterInfo;
import com.creaturelove.registry.LocalRegistry;
import com.creaturelove.registry.Registry;
import com.creaturelove.registry.RegistryFactory;
import com.creaturelove.server.tcp.VertxTcpServer;

import java.util.List;

// provider initialization
public class ProviderBootstrap {

    // initialization
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // rpc framework initialization
        RpcApplication.init();

        // global configuration
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // register service
        for(ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();

            // local registry
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // register to registry center
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

            try{
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName + " register failed", e);
            }
        }

        // start server
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
