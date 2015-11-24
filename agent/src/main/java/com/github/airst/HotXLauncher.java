package com.github.airst;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description: HotXLauncher
 * User: qige
 * Date: 15/2/7
 * Time: 16:42
 */
public class HotXLauncher {

    private static volatile ClassLoader hotXGlobalLoader;

    public static void agentmain(String args, Instrumentation inst) throws Exception {

        final ClassLoader hotXLoader;
        // 如果已经被启动则返回之前启动的classloader
        if (null != hotXGlobalLoader) {
            hotXLoader = hotXGlobalLoader;
        } else {
            String path = HotXLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            path = path.replace("hotX-agent.jar", "hotX-core.jar");
            hotXLoader = new URLClassLoader(new URL[]{new URL("file:" + path)}) {

                @Override
                protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    final Class<?> loadedClass = findLoadedClass(name);
                    if (loadedClass != null) {
                        return loadedClass;
                    }

                    try {
                        Class<?> aClass = findClass(name);
                        if (resolve) {
                            resolveClass(aClass);
                        }
                        return aClass;
                    } catch (Exception e) {
                        return super.loadClass(name, resolve);
                    }
                }

            };
            hotXGlobalLoader = hotXLoader;
        }

        Class<?> bClass = hotXLoader.loadClass("com.github.airst.HotXBoot");
        bClass.getMethod("boot", String.class, Instrumentation.class, Method.class).invoke(null, args, inst,
                HotXLauncher.class.getMethod("resetHotXClassLoader"));

        System.out.println("hotX boot finish..." + inst);
    }

    public synchronized static void resetHotXClassLoader() {
        hotXGlobalLoader = null;
    }
}
