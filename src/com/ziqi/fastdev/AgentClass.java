package com.ziqi.fastdev;


import com.ziqi.fastdev.CTools.CommonUtil;
import com.ziqi.fastdev.CTools.AgentUtil;

import java.lang.instrument.Instrumentation;

/**
 * Description: AgentClass
 * User: qige
 * Date: 15/2/7
 * Time: 16:42
 */
public class AgentClass {

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        CommonUtil.appName = args;
        CommonUtil.setInst(inst);
        System.out.println("init instrumentation..." + inst);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    boolean isOk = false;
                    for (ClassLoader classLoader : CommonUtil.searchClassLoader()) {
                        try {
                            Thread.currentThread().setContextClassLoader(classLoader);
                            AgentUtil.runServer();
                            CommonUtil.setClassLoader(classLoader);



                            isOk = true;
                            break;
                        } catch (Throwable e) {
                            isOk = false;
                            e.printStackTrace(System.out);
                            System.out.println(e.getMessage());
                        }
                    }

                    System.out.println("Engine start " + (isOk ? "success!" : "failed!"));

                } catch (Throwable e) {
                    e.printStackTrace(System.out);
                    System.out.println(e.getMessage());
                }
            }

        }).start();
    }

}
