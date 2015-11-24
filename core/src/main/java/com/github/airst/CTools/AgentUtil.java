package com.github.airst.CTools;

import com.github.airst.BTools.InnerUtil;
import com.github.airst.StaticContext;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Method;
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

        if(runTest == null) {
            Class<?> innerUtil;
            try {
                innerUtil = CommonUtil.attachClass(InnerUtil.class.getName(), StaticContext.getClassLoader());
            } catch (Throwable e) {
                innerUtil = StaticContext.getClassLoader().loadClass(InnerUtil.class.getName());
            }
            runTest = innerUtil.getDeclaredMethod("runTestMethod", Class.class, String[].class);
        }

        runTest.invoke(null, aClass, p);

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
