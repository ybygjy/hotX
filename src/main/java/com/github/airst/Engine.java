package com.github.airst;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.List;

/**
 * Description: Engine
 * User: qige
 * Date: 15/3/5
 * Time: 13:25
 */
public class Engine {


    public Engine(String[] args) throws Exception {

        // 解析配置文件
        Configure configure = analyzeConfigure(args);

        // 加载agent
        attachAgent(configure);

    }

    /*
     * 解析Configure
     */
    private Configure analyzeConfigure(String[] args) {
        final OptionParser parser = new OptionParser();
        parser.accepts("pid").withRequiredArg().ofType(int.class).required();
        parser.accepts("appName").withOptionalArg().ofType(String.class).required();

        final OptionSet os = parser.parse(args);
        final Configure configure = new Configure();
        configure.setPid((Integer) os.valueOf("pid"));
        configure.setAppName((String) os.valueOf("appName"));

        return configure;
    }

    public static void main(String[] args) throws Exception{

        try {
            new Engine(args);
        } catch (Throwable t) {
            System.err.println("start hotX failed");
            t.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    /*
     * 加载Agent
     */
    private void attachAgent(Configure configure) throws Exception {

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<?> vmdClass = loader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
        final Class<?> vmClass = loader.loadClass("com.sun.tools.attach.VirtualMachine");

        Object attachVmdObj = null;
        for (Object obj : (List<?>) vmClass.getMethod("list", (Class<?>[]) null).invoke(null, (Object[]) null)) {
            if ((vmdClass.getMethod("id", (Class<?>[]) null).invoke(obj, (Object[]) null)).equals(
                    Integer.toString(configure.getPid()))) {
                attachVmdObj = obj;
            }
        }

//        if (null == attachVmdObj) {
//            // throw new IllegalArgumentException("pid:" + configure.getJavaPid() + " not existed.");
//        }

        Object vmObj = null;
        try {
            if (null == attachVmdObj) { // 使用 attach(String pid) 这种方式
                vmObj = vmClass.getMethod("attach", String.class).invoke(null, "" + configure.getPid());
            } else {
                vmObj = vmClass.getMethod("attach", vmdClass).invoke(null, attachVmdObj);
            }
            String path = Engine.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            vmClass.getMethod("loadAgent", String.class).invoke(vmObj, path + "=" + configure.getAppName());
        } finally {
            if (null != vmObj) {
                vmClass.getMethod("detach", (Class<?>[]) null).invoke(vmObj, (Object[]) null);
            }
        }

    }

}
