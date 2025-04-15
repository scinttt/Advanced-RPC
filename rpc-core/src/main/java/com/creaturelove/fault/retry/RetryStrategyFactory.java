package com.creaturelove.fault.retry;

import com.creaturelove.spi.SpiLoader;

public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }

    // Default retry strategy
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    // retrieve instance
    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
