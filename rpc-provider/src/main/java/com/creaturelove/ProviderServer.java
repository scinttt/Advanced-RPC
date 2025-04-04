package com.creaturelove;

import com.creaturelove.config.RegistryConfig;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.model.ServiceMetaInfo;
import com.creaturelove.registry.LocalRegistry;
import com.creaturelove.registry.Registry;
import com.creaturelove.registry.RegistryFactory;
import com.creaturelove.server.HttpServer;
import com.creaturelove.server.VertxHttpServer;
import com.creaturelove.service.UserService;
import com.creaturelove.serviceImpl.UserServiceImpl;

/**
 * Hello world!
 *
 */
public class ProviderServer
{
    public static void main( String[] args )
    {
        //RPC Initialization
        RpcApplication.init();

        // register the service to Local Registry
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // register the service to Registry Center
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());

        try{
            registry.register(serviceMetaInfo);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        // start http server at provider side
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
