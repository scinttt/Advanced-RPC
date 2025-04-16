package com.creaturelove.rpcspringbootstarter.annotation;

import com.creaturelove.constant.RpcConstant;
import com.creaturelove.fault.retry.RetryStrategyKeys;
import com.creaturelove.fault.tolerant.TolerantStrategyKeys;
import com.creaturelove.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    // service interface class
    Class<?> interfaceClass() default void.class;

    // version
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    // load balancer
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    // Retry Strategy
    String retryStrategy() default RetryStrategyKeys.NO;

    // Tolerant Strategy
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    // Mock Call
    boolean mock() default false;
}
