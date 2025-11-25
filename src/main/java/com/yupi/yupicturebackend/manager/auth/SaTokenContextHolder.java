package com.yupi.yupicturebackend.manager.auth;

import java.util.HashMap;
import java.util.Map;

// 可选：考虑使用编程式鉴权，可以用ThreadLocal更灵活地表示上下文获取的逻辑
@Deprecated
public class SaTokenContextHolder {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    // 设置上下文数据
    public static void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    // 获取上下文数据
    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    // 清理上下文数据（防止内存泄漏）
    public static void clear() {
        CONTEXT.remove();
    }
}
