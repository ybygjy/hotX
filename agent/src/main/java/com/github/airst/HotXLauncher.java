package com.github.airst;

import com.github.airst.server.Server;
import com.google.common.collect.Sets;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Set;
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

    public static void main(String[] args) throws Exception {
        System.out.println("http server.");
        Server server = new Server(8080);
        server.bind();
        server.service();
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        try {
            final ClassLoader hotXLoader;
            // 如果已经被启动则返回之前启动的classloader
            ClassLoader webappClassLoader = null;
            if (null != hotXGlobalLoader) {
                System.out.println("hotX already boot...");
            } else {

                webappClassLoader = searchClassLoader(inst);

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
            bClass.getMethod("boot", String.class, Instrumentation.class, Method.class, ClassLoader.class, Class.class).invoke(null,
                    args,
                    inst,
                    HotXLauncher.class.getMethod("resetHotXClassLoader"),
                    Thread.currentThread().getContextClassLoader(),
                    searchApplicationClass(inst, webappClassLoader));
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
        Set<String> set = Sets.newHashSet();
        for (Class cls : classes) {
            ClassLoader classLoader = cls.getClassLoader();
            if (classLoader != null && classLoader.getClass() != null) {
                set.add(classLoader.getClass().getName());
                String simpleName = classLoader.getClass().getSimpleName();
                if (simpleName.endsWith("WebappClassLoader") || simpleName.endsWith("WebAppClassLoader")
                        || simpleName.endsWith("LaunchedURLClassLoader")) {
                    long endTimeMillis = System.currentTimeMillis();
                    System.out.println("Find ClassLoader costs: " + (endTimeMillis - startTimeMillis));
                    return classLoader;
                }
            }
        }
        return null;
    }

    public static Class searchApplicationClass(Instrumentation inst, ClassLoader webappClassLoader) {
        Class<?> bootApplication = null;
        try {
            System.out.println("webappClassLoader: " + webappClassLoader);
            bootApplication = webappClassLoader.loadClass("org.springframework.boot.autoconfigure.SpringBootApplication");
            System.out.println("bootApplication: " + bootApplication);

            if (bootApplication != null) {
                Class[] classes = inst.getAllLoadedClasses();
                for (Class cls : classes) {
                    if (cls.getAnnotation(bootApplication) != null && cls.getMethod("getApplicationContext") != null) {
                        Object applicationContext = cls.getMethod("getApplicationContext").invoke(null);
                        if(applicationContext != null) {
                            System.out.println("Find BootApplication named: " + cls + ", context:" + applicationContext);
                            return cls;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized static void resetHotXClassLoader() {
        hotXGlobalLoader = null;
    }
}
