package com.creaturelove.loadbalancer;

import com.creaturelove.model.ServiceMetaInfo;

import java.security.Provider;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer{
    private final Random random = new Random();

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList){
        int size = serviceMetaInfoList.size();
        if(size == 0){
            return null;
        }

        // only one service
        if(size == 1){
            return serviceMetaInfoList.get(0);
        }

        return serviceMetaInfoList.get(random.nextInt(size));
    }

}
