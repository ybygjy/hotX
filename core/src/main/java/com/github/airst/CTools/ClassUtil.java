package com.github.airst.CTools;

import com.github.airst.StaticContext;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * Description: HotCodeClassLoader
 * User: qige
 * Date: 15/2/6
 * Time: 14:47
 */
public class ClassUtil {

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
        ctClass.setName(ctClass.getName() + "_" + getTimestamp());
        return ctClass.toBytecode();

    }

    public static String getClassName(byte[] data) throws IOException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new ByteArrayInputStream(data));
        return ctClass.getName();
    }

    public static byte[] replaceClassName(byte[] data, String oldClass, String newClass) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(new ByteArrayInputStream(data));
        ctClass.replaceClassName(oldClass, newClass);
        return ctClass.toBytecode();
    }

    public static Class<?> loadClass(byte[] data, List<byte[]> subs) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClassM = pool.makeClass(new ByteArrayInputStream(data));

        long temp = getTimestamp();
        String oldM = ctClassM.getName();
        ctClassM.setName(ctClassM.getName() + "_" + temp);
        String newM = ctClassM.getName();

        for(byte[] bytes : subs) {
            CtClass ctClassS = pool.makeClass(new ByteArrayInputStream(bytes));
            ctClassS.replaceClassName(oldM, newM);

            String oldS = ctClassS.getName();
            ctClassS.setName(ctClassS.getName() + "_" + temp);
            String newS = ctClassS.getName();
            ctClassM.replaceClassName(oldS, newS);

            defineClass(ctClassS.toBytecode(), false);
        }

        return defineClass(ctClassM.toBytecode(), false);
    }

    public static long getTimestamp() {
        return new Date().getTime();
    }

}
