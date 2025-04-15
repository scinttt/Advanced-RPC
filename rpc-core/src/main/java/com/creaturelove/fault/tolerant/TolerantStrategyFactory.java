package com.creaturelove.fault.tolerant;

import com.creaturelove.spi.SpiLoader;

public class TolerantStrategyFactory {
    static{
        SpiLoader.load(TolerantStrategy.class);
    }

    // Default Tolerant Strategy
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    // Get Tolerant Strategy
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
