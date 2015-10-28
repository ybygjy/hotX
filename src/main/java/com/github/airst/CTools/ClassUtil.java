package com.github.airst.CTools;

import com.github.airst.StaticContext;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Description: HotCodeClassLoader
 * User: qige
 * Date: 15/2/6
 * Time: 14:47
 */
public class ClassUtil {

    public static int index = 0;

    public static Class<?> loadClass(byte[] data) throws Exception {
        return defineClass(data, true);
    }

    public static Class<?> defineClass(byte[] data, boolean rename) throws Exception {

        Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        method.setAccessible(true);
        if(rename) {
            data = changeClassName(data);
        }
        return (Class<?>) method.invoke(StaticContext.getClassLoader(), null, data, 0, data.length);
    }

    public static byte[] changeClassName(byte[] data) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new ByteArrayInputStream(data));
        ctClass.setName(ctClass.getName() + "_" + index++);
        return ctClass.toBytecode();

    }

    public static String getClassName(byte[] data) throws IOException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new ByteArrayInputStream(data));
        return ctClass.getName();
    }

}
