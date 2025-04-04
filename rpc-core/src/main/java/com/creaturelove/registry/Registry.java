package com.creaturelove.registry;

import com.creaturelove.config.RegistryConfig;
import com.creaturelove.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {

    // Initialization
    void init(RegistryConfig registryConfig);

    // Register Service
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    // Unregister Service
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    // ServiceDiscovery
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    // Service Destroy
    void destroy();
}
