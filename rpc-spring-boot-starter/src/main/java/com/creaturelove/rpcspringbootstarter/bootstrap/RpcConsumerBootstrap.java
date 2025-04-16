package com.creaturelove.rpcspringbootstarter.bootstrap;

import com.creaturelove.proxy.ServiceProxyFactory;
import com.creaturelove.rpcspringbootstarter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {
    // Execute after bean initialization, inject the service
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException{
        Class<?> beanClass = bean.getClass();

        // iterate all the fields of the object
        Field[] declaredFields = beanClass.getDeclaredFields();

        for(Field field : declaredFields){
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if(rpcReference != null){
                // generate proxy object for fields
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if(interfaceClass == void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try{
                    field.set(bean, proxyObject);
                    field.setAccessible(false);
                }catch(IllegalAccessException e){
                    throw new RuntimeException("Failed to inject proxy object for field: " + field.getName(), e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
