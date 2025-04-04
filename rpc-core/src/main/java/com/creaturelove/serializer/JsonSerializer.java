package com.creaturelove.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.creaturelove.model.RpcRequest;
import com.creaturelove.model.RpcResponse;

import java.io.IOException;


public class JsonSerializer implements Serializer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if(obj instanceof RpcRequest){
            return handleRequest((RpcRequest) obj, classType);
        }
        if(obj instanceof RpcResponse){
            return handleResponse((RpcResponse) obj, classType);
        }
        return obj;
    }

    // object 原始对象会被擦除，deserialization 是会被当做 linkedHashMap 回不去原始对象，这里进行额外处理
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException{
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        // loop handle every args type
        for(int i=0; i<parameterTypes.length; i++){
            Class<?> clazz = parameterTypes[i];
            // if type is different, rehandle the type
            if(!clazz.isAssignableFrom(args[i].getClass())){
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException{
        // handle response data
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}
