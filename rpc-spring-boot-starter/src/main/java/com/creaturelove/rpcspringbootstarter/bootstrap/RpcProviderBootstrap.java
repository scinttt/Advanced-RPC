package com.creaturelove.rpcspringbootstarter.bootstrap;

import com.creaturelove.RpcApplication;
import com.creaturelove.config.RegistryConfig;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.model.ServiceMetaInfo;
import com.creaturelove.registry.LocalRegistry;
import com.creaturelove.registry.Registry;
import com.creaturelove.registry.RegistryFactory;
import com.creaturelove.rpcspringbootstarter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        RpcService rpcService = beanClass.getAnnotation(RpcService.class);

        if (rpcService != null) {
            // need to register service
            // 1. get the basic info of the service
            Class<?> interfaceClass = rpcService.interfaceClass();

            // default value process
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 2. register service
            // local registry
            LocalRegistry.register(serviceName, beanClass);

            // global Configuration
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            // register to registry center
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceVersion(serviceVersion);
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " register failed", e);
            }

        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
