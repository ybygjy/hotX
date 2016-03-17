package com.github.airst;

import com.github.airst.server.Server;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.macosx.MacOSXDebuggerLocal;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Description: HotXLauncher
 * User: qige
 * Date: 15/2/7
 * Time: 16:42
 */
public class HotXLauncher {

    private static volatile ClassLoader hotXGlobalLoader;

    private static final String loaderName = "com.github.airst.classLoader.HotXClassLoader";

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        try {
            final ClassLoader hotXLoader;
            // 如果已经被启动则返回之前启动的classloader
            if (null != hotXGlobalLoader) {
                System.out.println("hotX already boot...");
            } else {

                ClassLoader webappClassLoader = searchClassLoader(inst);

                if (webappClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(webappClassLoader);

                    String path = HotXLauncher.class.getProtectionDomain().getCodeSource().getLocation().getFile();
                    path = path.replace("hotX-agent.jar", "hotX-core.jar");

                    Class<?> aClass = attachLoader(path, webappClassLoader);
                    Method get = aClass.getMethod("get", String.class, ClassLoader.class);

                    hotXLoader = (ClassLoader) get.invoke(null, path, webappClassLoader);

                    hotXGlobalLoader = hotXLoader;
                } else {
                    System.out.println("can't find WebappClassLoader");
                    return;
                }

            }

            Class<?> bClass = hotXGlobalLoader.loadClass("com.github.airst.HotXBoot");
            bClass.getMethod("boot", String.class, Instrumentation.class, Method.class, ClassLoader.class).invoke(null,
                    args,
                    inst,
                    HotXLauncher.class.getMethod("resetHotXClassLoader"),
                    Thread.currentThread().getContextClassLoader());
            System.out.println("hotX boot finish...");
        } catch (Throwable t) {
            hotXGlobalLoader = null;
            t.printStackTrace(System.out);
        }

    }

    public static Class<?> attachLoader(String path, ClassLoader classLoader) throws Exception {
        try {
            return classLoader.loadClass(loaderName);
        } catch (ClassNotFoundException e) {
            JarFile jar = new JarFile(path, true);
            Method getBytes = JarFile.class.getDeclaredMethod("getBytes", ZipEntry.class);
            getBytes.setAccessible(true);
            byte[] data = (byte[]) getBytes.invoke(jar, jar.getEntry(loaderName.replace(".", "/") + ".class"));

            Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);
            return (Class<?>) method.invoke(classLoader, null, data, 0, data.length);
        }
    }

    public static ClassLoader searchClassLoader(Instrumentation inst) {
        long startTimeMillis = System.currentTimeMillis();
        Class[] classes = inst.getAllLoadedClasses();
        for (Class cls : classes) {
            ClassLoader classLoader = cls.getClassLoader();
            if (classLoader != null && classLoader.getClass() != null) {
                String simpleName = classLoader.getClass().getSimpleName();
                if (simpleName.endsWith("WebappClassLoader") || simpleName.endsWith("WebAppClassLoader")) {

                    long endTimeMillis = System.currentTimeMillis();
                    System.out.println("Find ClassLoader costs: " + (endTimeMillis - startTimeMillis));

                    return classLoader;
                }
            }
        }
        return null;
    }

    public synchronized static void resetHotXClassLoader() {
        hotXGlobalLoader = null;
    }
}
