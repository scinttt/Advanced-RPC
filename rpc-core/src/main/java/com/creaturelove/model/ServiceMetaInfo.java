package com.creaturelove.model;

import cn.hutool.core.util.StrUtil;

public class ServiceMetaInfo {
    private String serviceName;

    private String serviceVersion = "1.0";

    private String serviceHost;

    private Integer servicePort;

    private String serviceGroup = "default";

    public String getServiceKey(){
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    public String getServiceNodeKey(){
        return String.format("%s/%s:%s", getServiceNodeKey(), serviceHost, servicePort);
    }

    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost, "http")){
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
