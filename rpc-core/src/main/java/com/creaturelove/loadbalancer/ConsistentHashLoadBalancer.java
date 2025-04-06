package com.creaturelove.loadbalancer;

import com.creaturelove.model.ServiceMetaInfo;

import java.security.Provider;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer{

    // consistent hash
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    // virtual node number
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList){
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }

        // construct virtual node circle
        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList){
            for(int i=0; i<VIRTUAL_NODE_NUM; i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // retrieve the hash value of the request
        int hash = getHash(requestParams);

        // select the node with the smallest hash value larger than the request hash value
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry == null){
            // if there is not a node with hash value larger than request, return the head ndoe
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    // Hash Algorithm
    private int getHash(Object key){
        return key.hashCode();
    }
}


