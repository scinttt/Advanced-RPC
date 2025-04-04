package com.creaturelove;

import com.creaturelove.config.RegistryConfig;
import com.creaturelove.config.RpcConfig;
import com.creaturelove.constant.RpcConstant;
import com.creaturelove.registry.Registry;
import com.creaturelove.registry.RegistryFactory;
import com.creaturelove.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

// RPC framework
// Store all the environment variables
// Singleton Pattern
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    // Initialize the customized configuration
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        // Initialize registry center
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
    }

    // Initialization
    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }

        init(newRpcConfig);
    }

    // Classic Singleton Retrieval
    public static RpcConfig getRpcConfig(){
        if(rpcConfig == null){
            synchronized (RpcApplication.class){
                if(rpcConfig == null){
                    init();
                }
            }
        }

        return rpcConfig;
    }
}
