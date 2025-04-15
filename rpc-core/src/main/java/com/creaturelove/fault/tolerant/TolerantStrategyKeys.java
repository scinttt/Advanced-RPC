package com.creaturelove.fault.tolerant;

public interface TolerantStrategyKeys {
    // fail back
    String FAIL_BACK = "failBack";

    // Fail fast
    String FAIL_FAST = "failFast";

    // Fail Over
    String FAIL_OVER = "failOver";

    // Fail Safe
    String FAIL_SAFE = "failSafe";
}
