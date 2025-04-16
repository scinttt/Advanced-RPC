package com.creaturelove.rpcspringbootstarter.annotation;

import com.creaturelove.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.creaturelove.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.creaturelove.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {

    // need to start server
    boolean needServer() default true;
}
