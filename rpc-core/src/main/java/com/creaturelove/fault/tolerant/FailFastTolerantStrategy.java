package com.creaturelove.fault.tolerant;

import com.creaturelove.model.RpcResponse;

import java.util.Map;

// fast failure strategy (immediate told outside caller that the service is unavailable)
public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("Service is unavailable", e);
    }
}
