package com.creaturelove.rpcspringbootstarter.annotation;

import com.creaturelove.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    // service interface class
    Class<?> interfaceClass() default void.class;

    // version
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
