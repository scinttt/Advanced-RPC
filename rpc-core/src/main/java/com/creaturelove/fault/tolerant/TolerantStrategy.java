package com.creaturelove.fault.tolerant;

import com.creaturelove.model.RpcRequest;
import com.creaturelove.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
