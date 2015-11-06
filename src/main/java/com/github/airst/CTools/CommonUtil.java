package com.github.airst.CTools;

import com.github.airst.StaticContext;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: HotCodeUtil
 * User: qige
 * Date: 15/2/6
 * Time: 13:41
 */
public class CommonUtil {

    //project code
//    private static final Logger logger = LoggerFactory.getLogger(HotCodeUtil.class);

    public static byte[] readFile(File classFile) throws Exception {
        InputStream inputStream = new FileInputStream(classFile);
        int count = (int) classFile.length();
        byte[] code = readStream(inputStream, count);
        inputStream.close();
        return code;
    }

    public static byte[] readStream(InputStream inputStream, int size) throws Exception {
        byte[] data = new byte[size];
        int readCount = 0;
        while (readCount < size) {
            readCount += inputStream.read(data, readCount, size - readCount);
        }
        return data;
    }

    public static byte[] readAll(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
    /**
     * ====================================================
     * Class Load Part
     * ====================================================
     */
    public static Class<?> attachClass(byte[] data, ClassLoader classLoader) throws Exception {

        Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        method.setAccessible(true);
        return (Class<?>) method.invoke(classLoader, null, data, 0, data.length);
    }

    public static Class<?> attachClass(String className, ClassLoader classLoader) throws Exception {
        className = "/" + className.replace(".", "/") + ".class";
        InputStream stream = CommonUtil.class.getResourceAsStream(className);
        byte[] data = readStream(stream, stream.available());
        return attachClass(data, classLoader);
    }

    public static Set<ClassLoader> searchClassLoader() {
        long startTimeMillis = System.currentTimeMillis();
        Class[] classes = StaticContext.getInst().getAllLoadedClasses();
        Set<ClassLoader> classLoaders = new HashSet<ClassLoader>();
        for (Class cls : classes) {
            ClassLoader classLoader = cls.getClassLoader();
            if (classLoader != null && classLoader.getClass() != null) {
                String simpleName = classLoader.getClass().getSimpleName();
                if (simpleName.endsWith("WebappClassLoader") || simpleName.endsWith("WebAppClassLoader")) {
                    classLoaders.add(classLoader);
                }
            }
        }
        long endTimeMillis = System.currentTimeMillis();
        System.out.println("Find ClassLoader costs: " + (endTimeMillis - startTimeMillis));
        return classLoaders;
    }

}
