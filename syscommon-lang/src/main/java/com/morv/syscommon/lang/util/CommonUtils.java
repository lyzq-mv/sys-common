package com.morv.syscommon.lang.util;

import java.util.function.Supplier;

public class CommonUtils {
    public static <T> T getFirstNoNullValue(Supplier<T>... suppliers) {
        T result = null;
        for (Supplier<T> supplier : suppliers) {
            result = supplier.get();
            if (result != null) {
                return result;
            }
        }
        return result;
    }
}
