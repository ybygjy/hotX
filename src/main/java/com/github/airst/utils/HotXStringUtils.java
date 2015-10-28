package com.github.airst.utils;

/**
 * Description: HotXStringUtils
 * User: qige
 * Date: 15/10/28
 * Time: 下午2:22
 */
public class HotXStringUtils {

    /**
     * 获取异常的原因描述
     *
     * @param t 异常
     * @return 异常原因
     */
    public static String getCauseMessage(Throwable t) {
        if (null != t.getCause()) {
            return getCauseMessage(t.getCause());
        }
        return t.getMessage();
    }
}
