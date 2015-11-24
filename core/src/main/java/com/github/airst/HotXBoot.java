package com.github.airst;

import com.github.airst.CTools.CommonUtil;
import com.github.airst.CTools.StringUtil;
import com.github.airst.CTools.server.Server;
import org.springframework.util.StringUtils;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

/**
 * Description: HotXBoot
 * User: qige
 * Date: 15/11/5
 * Time: 下午8:13
 */
public class HotXBoot {

    public static Server server = null;

    private static Method resetClassLoaderMethod = null;

    public static synchronized void boot(String appName, Instrumentation inst, Method resetMethod) {
        try {
            resetClassLoaderMethod = resetMethod;
            if(!StringUtil.isBlank(appName) && !StringUtil.isNullStr(appName)) {
                StaticContext.setAppName(appName);
            }
            if(StaticContext.getInst() == null) {
                StaticContext.setInst(inst);

                boolean isOk = false;
                for (ClassLoader classLoader : CommonUtil.searchClassLoader()) {
                    try {
                        Thread.currentThread().setContextClassLoader(classLoader);
                        StaticContext.setClassLoader(classLoader);

                        start();

                        isOk = true;
                        break;
                    } catch (Throwable e) {
                        isOk = false;
                        e.printStackTrace(System.out);
                        System.out.println(e.getMessage());
                    }
                }

                System.out.println("Engine start " + (isOk ? "success!" : "failed!"));
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    private static void start() throws Exception {
        if(server == null) {
            server = new Server(8080);
        }
        server.bind();
        server.service();
    }

    public static void shutdown() throws Exception{
        server.shutdown();
        server = null;
        StaticContext.dispose();

        resetClassLoaderMethod.invoke(null);
    }
}
