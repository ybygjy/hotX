package com.github.airst.CTools;

import com.github.airst.StaticContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;
import java.lang.reflect.Method;
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

    public static Object autowire(Object applicationContext, Class targetClass, boolean check) throws Exception {

        Class<?> aClass = StaticContext.getClassLoader().loadClass(WebApplicationContext.class.getName());
        Method getAutowireCapableBeanFactory = aClass.getMethod("getAutowireCapableBeanFactory");
        Object autowireCapableBeanFactory = getAutowireCapableBeanFactory.invoke(applicationContext);

        Class<?> bClass = StaticContext.getClassLoader().loadClass(AutowireCapableBeanFactory.class.getName());
        Method autowire = bClass.getMethod("autowire", Class.class, int.class, boolean.class);
        return autowire.invoke(autowireCapableBeanFactory, targetClass, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, check);
    }

    public static Object getBean(Object applicationContext, String beanName) throws Exception {
        Class<?> aClass = StaticContext.getClassLoader().loadClass(BeanFactory.class.getName());
        Method getBean = aClass.getMethod("getBean", String.class);
        return getBean.invoke(applicationContext, beanName);
    }

    public static boolean containsBean(Object applicationContext, String beanName) throws Exception {
        Class<?> aClass = StaticContext.getClassLoader().loadClass(BeanFactory.class.getName());
        Method getBean = aClass.getMethod("containsBean", String.class);
        return (Boolean) getBean.invoke(applicationContext, beanName);
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
        try {
            return StaticContext.getClassLoader().loadClass("com.tmall.legao.client.service.query.LegaoQueryClient");
        } catch (Exception e) {
            className = "/" + className.replace(".", "/") + ".class";
            InputStream stream = CommonUtil.class.getResourceAsStream(className);
            byte[] data = readStream(stream, stream.available());
            return attachClass(data, classLoader);
        }
    }

}
