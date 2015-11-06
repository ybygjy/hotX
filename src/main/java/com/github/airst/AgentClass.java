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

    public static void agentmain(String args, Instrumentation inst) throws Exception {

        ClassLoader hotXLoader = new URLClassLoader(new URL[]{new URL("file:" +
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

        Class<?> aClass = hotXLoader.loadClass("com.github.airst.StaticContext");
        aClass.getMethod("setAppName", String.class).invoke(null, args);
        aClass.getMethod("setInst", Instrumentation.class).invoke(null, inst);

        Class<?> bClass = hotXLoader.loadClass("com.github.airst.HotXBoot");
        bClass.getMethod("boot").invoke(null);

        System.out.println("hotX boot finish..." + inst);
    }

}
