package com.creaturelove.fault.retry;

import com.creaturelove.model.RpcResponse;

import java.util.concurrent.Callable;

// retry strategy
public interface RetryStrategy {
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
