package com.creaturelove.bootstrap;

import com.creaturelove.RpcApplication;

public class ConsumerBootstrap {
    // initialization
    public static void init(){
        // rpc framework initialization(Configuration & Registry Center)
        RpcApplication.init();
    }
}
