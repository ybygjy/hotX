package com.github.airst;


import com.github.airst.CTools.CommonUtil;
import com.github.airst.CTools.AgentUtil;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description: AgentClass
 * User: qige
 * Date: 15/2/7
 * Time: 16:42
 */
public class AgentClass {

    private static volatile ClassLoader hotXGlobalLoader;

    public static void agentmain(String args, Instrumentation inst) throws Exception {

        final ClassLoader hotXLoader;
        // 如果已经被启动则返回之前启动的classloader
        if (null != hotXGlobalLoader) {
            hotXLoader = hotXGlobalLoader;
        } else {

            hotXLoader = new URLClassLoader(new URL[]{new URL("file:" +
                    AgentClass.class.getProtectionDomain().getCodeSource().getLocation().getFile())}) {

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
        bClass.getMethod("boot", String.class, Instrumentation.class).invoke(null, args, inst);

        System.out.println("hotX boot finish..." + inst);
    }

}
