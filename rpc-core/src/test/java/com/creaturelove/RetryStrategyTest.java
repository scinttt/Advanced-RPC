package com.creaturelove;


import com.creaturelove.fault.retry.NoRetryStrategy;
import com.creaturelove.fault.retry.RetryStrategy;
import com.creaturelove.model.RpcResponse;
import org.junit.Test;

public class RetryStrategyTest {

    RetryStrategy retryStrategy = new NoRetryStrategy();

    @Test
    public void doRetry(){
        try{
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("test Retry");
                throw new RuntimeException("Mock Retry Fail");
            });
            System.out.println(rpcResponse);
        }catch(Exception e){
            System.out.println("Retry fail multiple times");
            e.printStackTrace();
        }
    }
}
