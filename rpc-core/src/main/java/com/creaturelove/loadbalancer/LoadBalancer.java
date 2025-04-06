package com.creaturelove.loadbalancer;

import com.creaturelove.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoadBalancer {
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
