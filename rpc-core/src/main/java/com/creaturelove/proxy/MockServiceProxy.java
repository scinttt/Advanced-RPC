package com.creaturelove.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServiceProxy implements InvocationHandler
{
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
        // generate default object based on the method's return type
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke{}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> type){
        // primitive type
        if(type.isPrimitive()){
            if(type == boolean.class)
                return false;
            else if(type == short.class)
                return (short) 0;
            else if(type == int.class)
                return 0;
            else if(type == long.class)
                return 0L;
        }

        // object type
        return null;
    }
}
