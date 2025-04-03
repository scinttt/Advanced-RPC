package com.creaturelove.proxy;

import java.lang.reflect.Proxy;

// Service Proxy Factory is used to create proxy object
public class ServiceProxyFactory {
    // retrieve proxy object based on service class
    public static <T> T getProxy(Class<T> serviceClass){
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
}
