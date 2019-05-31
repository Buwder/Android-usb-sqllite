package com.zskx.util;

/**
 * 字符串处理类
 */
public class StringUtil {

    /**
     * 判断字符串是否不为空
     *
     * @return 返回值  true 不为空   false 为空
     */
    public static boolean isNoNull(String str) {
        boolean lean = false;
        if (str != null && !str.equals("") && !str.equals("null")) {
            lean = true;
        }
        return lean;
    }
}
