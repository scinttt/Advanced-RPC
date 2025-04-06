package com.creaturelove.loadbalancer;

public interface LoadBalancerKeys {
    // round robin
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";
}
