package com.creaturelove.config;

import com.creaturelove.serializer.Serializer;
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
}
