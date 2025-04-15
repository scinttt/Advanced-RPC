package com.creaturelove.config;

import com.creaturelove.fault.retry.RetryStrategyKeys;
import com.creaturelove.fault.tolerant.TolerantStrategyKeys;
import com.creaturelove.loadbalancer.LoadBalancerKeys;
import com.creaturelove.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    // Rpc Name
    private String name = "advanced-rpc";

    // version
    private String version = "1.0";

    // Host name
    private String serverHost = "localhost";

    // server port number
    private Integer serverPort = 8080;

    // mock call
    private boolean mock = false;

    // Serializer
    private String serializer = SerializerKeys.JDK;

    // Registry Configuration
    private RegistryConfig registryConfig = new RegistryConfig();

    // Load Balancer
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    // retry strategy
    private String retryStrategy = RetryStrategyKeys.NO;

    // fail tolerant strategy
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
