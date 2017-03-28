package com.github.airst;

import com.github.airst.database.DBExecutor;
import org.springframework.context.ApplicationContext;

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

    private static ApplicationContext applicationContext;

    private static DBExecutor dbExecutor;

    private static Class applicationClass;

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

    public static ApplicationContext getApplicationContext() {
        if(applicationContext == null) {
            throw new RuntimeException("Can't use this function,If it's are a Spring boot App,Please make sure you have a static getApplicationContext() function in your Main-Class!");
        }
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        StaticContext.applicationContext = applicationContext;
    }

    public static DBExecutor getDbExecutor() {
        return dbExecutor;
    }

    public static void setDbExecutor(DBExecutor dbExecutor) {
        StaticContext.dbExecutor = dbExecutor;
    }

    public static Class getApplicationClass() {
        return applicationClass;
    }

    public static void setApplicationClass(Class applicationClass) {
        StaticContext.applicationClass = applicationClass;
    }

    public static void dispose() {
        inst = null;
        classLoader = null;
        applicationContext = null;
        dbExecutor = null;
        applicationClass = null;
    }
}
