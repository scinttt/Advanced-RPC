package com.creaturelove.model;

import com.creaturelove.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    // service name
    private String serviceName;

    // method name
    private String methodName;

    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    // parameter type array
    private Class<?>[] parameterTypes;

    // parameter array
    private Object[] args;
}
