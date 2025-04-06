package com.creaturelove.loadbalancer;

import com.creaturelove.spi.SpiLoader;

public class LoadBalancerFactory {
    static{
        SpiLoader.load(LoadBalancer.class);
    }

    // Default load balancer
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    // Retrieve Instance
    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
