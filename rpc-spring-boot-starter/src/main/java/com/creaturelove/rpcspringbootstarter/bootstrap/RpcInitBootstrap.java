package com.creaturelove.rpcspringbootstarter.bootstrap;

import com.creaturelove.RpcApplication;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.rpcspringbootstarter.annotation.EnableRpc;
import com.creaturelove.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){
        // get EnableRPC 's filed
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // rpc framework initialization (setup configuration and Registry Center)
        RpcApplication.init();

        // global configuration
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // start server
        if(needServer){
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        }else{
            log.info("Don't start RPC server");
        }
    }
}
