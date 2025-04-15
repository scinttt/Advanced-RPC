package com.creaturelove.fault.tolerant;

import com.creaturelove.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

// do nothing

@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("safe exception handling", e);
        return new RpcResponse();
    }
}
