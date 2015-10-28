package com.github.airst;


import com.github.airst.CTools.CommonUtil;
import com.github.airst.CTools.AgentUtil;

import java.lang.instrument.Instrumentation;

/**
 * Description: AgentClass
 * User: qige
 * Date: 15/2/7
 * Time: 16:42
 */
public class AgentClass {

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        StaticContext.appName = args;
        StaticContext.setInst(inst);
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
                            StaticContext.setClassLoader(classLoader);



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
