package com.github.airst;

import java.lang.instrument.Instrumentation;

/**
 * Description: StaticContext
 * User: qige
 * Date: 15/10/28
 * Time: 下午2:41
 */
public class StaticContext {

    private static Instrumentation inst;

    private static ClassLoader classLoader;

    public static String appName = "";

    public static Instrumentation getInst() {
        return inst;
    }

    public static void setInst(Instrumentation inst) {
        StaticContext.inst = inst;
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    public static void setClassLoader(ClassLoader classLoader) {
        StaticContext.classLoader = classLoader;
    }

    public static String getAppName() {
        return appName;
    }

    public static void setAppName(String appName) {
        StaticContext.appName = appName;
    }
}
