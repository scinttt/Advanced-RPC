package com.creaturelove.model;

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
}
