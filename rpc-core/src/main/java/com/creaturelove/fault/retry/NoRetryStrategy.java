package com.creaturelove.fault.retry;

import com.creaturelove.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class NoRetryStrategy implements RetryStrategy{

    // No retry
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception{
        return callable.call();
    }
}
