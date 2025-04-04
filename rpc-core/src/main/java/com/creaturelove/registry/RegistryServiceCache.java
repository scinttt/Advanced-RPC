package com.creaturelove.registry;

import com.creaturelove.model.ServiceMetaInfo;

import java.util.List;

// Local cache for Registry Center
public class RegistryServiceCache {
    // Service Cache
    List<ServiceMetaInfo> serviceCache;

    void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache = newServiceCache;
    }

    List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }

    // clear cache
    void clearCache(){
        this.serviceCache = null;
    }
}
