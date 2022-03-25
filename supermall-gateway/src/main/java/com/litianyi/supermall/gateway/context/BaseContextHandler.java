package com.litianyi.supermall.gateway.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author litianyi
 */
public class BaseContextHandler {

    private static final ThreadLocal<Map<String, Object>> threadContext = ThreadLocal.withInitial(HashMap::new);

    private BaseContextHandler() {

    }

    /**
     * 取得thread context Map的实例。
     *
     * @return thread context Map的实例
     */
    public static Map<String, Object> getContextMap() {
        return threadContext.get();
    }

    public static void remove() {
        getContextMap().clear();
    }

}
