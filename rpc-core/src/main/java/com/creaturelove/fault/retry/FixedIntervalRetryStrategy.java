package com.creaturelove.fault.retry;

import com.creaturelove.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{

    // fixed interval retry
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException{
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener(){
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("Retry attempt times: {}", attempt.getAttemptNumber());
                    }
                })
                .build();

        return retryer.call(callable);
    }
}
