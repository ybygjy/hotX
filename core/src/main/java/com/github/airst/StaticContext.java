package com.github.airst;

import com.github.airst.database.DBExecutor;
import org.springframework.web.context.WebApplicationContext;

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

    private static WebApplicationContext webApplicationContext;

    private static DBExecutor dbExecutor;

    private static String appName = null;

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

    public static WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public static void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        if(webApplicationContext == null) {
            throw new RuntimeException("webApplicationContext is null");
        }
        StaticContext.webApplicationContext = webApplicationContext;
    }

    public static DBExecutor getDbExecutor() {
        return dbExecutor;
    }

    public static void setDbExecutor(DBExecutor dbExecutor) {
        StaticContext.dbExecutor = dbExecutor;
    }

    public static void dispose() {
        inst = null;
        classLoader = null;
        webApplicationContext = null;
        dbExecutor = null;
    }
}
