package com.github.airst.CTools;

import com.github.airst.StaticContext;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Description: HotCodeUtil
 * User: qige
 * Date: 15/2/6
 * Time: 13:41
 */
public class AgentUtil {

    //project code
//    private static final Logger logger = LoggerFactory.getLogger(HotCodeUtil.class);

    private static Method runTest;

    public static void runTestMethod(File classFile, String[] p) throws Exception {
        runTestMethod(CommonUtil.readFile(classFile), p, null);
    }

    //test method need be [public static void test()]
    public static void runTestMethod(byte[] code, String[] p, List<byte[]> subClasses) throws Exception {
        Class<?> aClass;
        if(subClasses != null) {
            aClass = ClassUtil.loadClass(code, subClasses);
        } else {
            aClass = ClassUtil.loadClass(code);
        }
        if(StaticContext.getApplicationContext() == null) {
            throw new RuntimeException("Can't use this function,If it's are a Spring boot App,Please make sure you have a static getApplicationContext() function in your Main-Class!");
        }
        Object bean = CommonUtil.autowire(StaticContext.getApplicationContext(), aClass, true);

        Method method = aClass.getMethod("test", String[].class);
        if(Modifier.isStatic(method.getModifiers())) {
            method.invoke(null, (Object)p);
        } else {
            method.invoke(bean, (Object)p);
        }

    }


    public static void replaceClassFile(File classFile) throws Exception {
        replaceClassFile(CommonUtil.readFile(classFile));
    }

    public static void replaceClassFile(byte[] newCode) throws Exception {
        ClassLoader classLoader;
        String className = ClassUtil.getClassName(newCode);
        if(!className.startsWith("com.github.airst")) {
            classLoader = StaticContext.getClassLoader();
        } else {
            classLoader = AgentUtil.class.getClassLoader();
        }

        try {
            Class targetClass = classLoader.loadClass(className);
            ClassDefinition classDef = new ClassDefinition(targetClass, newCode);
            StaticContext.getInst().redefineClasses(classDef);
        } catch (ClassNotFoundException e) {
            CommonUtil.attachClass(newCode, classLoader);
        }
    }

}
