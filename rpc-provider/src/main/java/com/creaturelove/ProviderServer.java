package com.creaturelove;

import com.creaturelove.bootstrap.ProviderBootstrap;
import com.creaturelove.model.ServiceRegisterInfo;
import com.creaturelove.service.UserService;
import com.creaturelove.serviceImpl.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class ProviderServer
{
    public static void main( String[] args )
    {
        // services needed to be registered
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(
                UserService.class.getName(),
                UserServiceImpl.class
        );
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // service provider initialization
        ProviderBootstrap.init(serviceRegisterInfoList);

//
//        //RPC Initialization
//        RpcApplication.init();
//
//        // register the service to Local Registry
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName, UserServiceImpl.class);
//
//        // register the service to Registry Center
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//
//        try{
//            registry.register(serviceMetaInfo);
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }
//
//        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        vertxTcpServer.doStart(8080);

//        // start http server at provider side
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
