package com.creaturelove.fault.tolerant;

import com.creaturelove.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

// Degrade to other service
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // get degrade service and call it
        return null;
    }
}
