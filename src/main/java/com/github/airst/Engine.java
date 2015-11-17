package com.github.airst;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;
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
        parser.accepts("pid").withRequiredArg().ofType(int.class);
        parser.accepts("appName").withRequiredArg().ofType(String.class);

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

        autoSetPid(configure);

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

    private void autoSetPid(Configure configure) throws IOException {
        //String cmd = "ifconfig";//ok
        //String cmd = "sar -u 1 1| awk 'NR==4 {print $8}'";//空白。管道不支持
        String c = "ps -ef | grep java | grep tomcat";
        String[] cmd = {"/bin/sh", "-c", c};//ok
        if(configure.getPid() == null) {
            // 使用Runtime来执行command，生成Process对象
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            // 取得命令结果的输出流
            InputStream is = process.getInputStream();
            // 用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(is);
            // 用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if(line.contains(c)) {
                    continue;
                }
                line = line.trim().split("\\s+")[1];
                configure.setPid(Integer.valueOf(line.trim()));
                System.out.println(configure.getPid());
            }
            is.close();
            isr.close();
            br.close();
        }
    }

}
