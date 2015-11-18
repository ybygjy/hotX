package com.github.airst;

import com.github.airst.CTools.CommonUtil;
import com.github.airst.CTools.server.Server;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * Description: HotXBoot
 * User: qige
 * Date: 15/11/5
 * Time: 下午8:13
 */
public class HotXBoot {

    public static Server server = null;

    public static synchronized void boot(String appName, Instrumentation inst) {
        try {

            if(appName != null && !"null".equals(appName) && !"".equals(appName)) {
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
    }
}
