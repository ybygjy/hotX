package com.ziqi.fastdev.CTools;

import com.ziqi.fastdev.CTools.server.Server;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Method;

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
        AgentUtil.runTestMethod(CommonUtil.readFile(classFile), p);
    }

    //test method need be [public static void test()]
    public static void runTestMethod(byte[] code, String[] p) throws Exception {
        Class<?> aClass = ClassUtil.loadClass(code);

        if(runTest == null) {
            Class<?> innerUtil = CommonUtil.attachClass("com.ziqi.fastdev.BTools.InnerUtil", CommonUtil.getClassLoader());
            runTest = innerUtil.getDeclaredMethod("runTestMethod", Class.class, String[].class);
        }

        runTest.invoke(null, aClass, p);

    }

    public static void replaceClassFile(File classFile) throws Exception {
        AgentUtil.replaceClassFile(CommonUtil.readFile(classFile));
    }

    public static void replaceClassFile(byte[] newCode) throws Exception {
        ClassLoader classLoader = CommonUtil.getClassLoader();
        Class targetClass = classLoader.loadClass(ClassUtil.getClassName(newCode));

        ClassDefinition classDef = new ClassDefinition(targetClass, newCode);
        CommonUtil.getInst().redefineClasses(classDef);
    }

    public static void runServer() {
        new Thread(new Server(8080)).start();
    }

}
