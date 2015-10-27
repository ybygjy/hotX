package com.ziqi.fastdev;

import com.sun.tools.attach.VirtualMachine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Description: Engine
 * User: qige
 * Date: 15/3/5
 * Time: 13:25
 */
public class Engine {

    public static void main(String[] args) throws Exception{

        String appName = "";
        VirtualMachine virtualMachine = null;
        if(args.length < 1) {
            System.err.print("please input appName:");
            appName = String.valueOf(new Scanner(System.in).nextLine());
        } else {
            appName = args[0];
        }
        String pid = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/home/admin/" + appName + "/.default/catalina.pid"));
            pid = reader.readLine();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.print("try other file....");
            try {
                File[] pidFiles = new File("/tmp/hsperfdata_admin").listFiles();
                if (pidFiles != null) {
                    for (File file : pidFiles) {
                        if (file.isFile()) {
                            pid = file.getName();
                        }
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if(pid == null) {
            System.err.print("please input pid:");
            pid = new Scanner(System.in).next();
        }

        String path = Engine.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        System.out.println("pid: " + pid + ", AgentJar: " + path);

        try {
            virtualMachine = VirtualMachine.attach(pid);
        } catch (Exception e) {
            System.err.print("please input pid:");
            pid = new Scanner(System.in).next();
            virtualMachine = VirtualMachine.attach(pid);
        }
        if(virtualMachine != null) {
            virtualMachine.loadAgent(path + "=" + appName);
            virtualMachine.detach();
        }
    }

    public static VirtualMachine autoSearch() throws Exception {
        List<String> cmds = new ArrayList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add("ps -ef | grep java | grep tomcat | cut -c10-14");
        ProcessBuilder pd = new ProcessBuilder(cmds);
        Process process = pd.start();
        InputStreamReader stream = new InputStreamReader(process.getInputStream());
        BufferedReader inputBufferedReader = new  BufferedReader(stream);
        String temp = inputBufferedReader.readLine();
        if(temp != null) {
            String[] strings = temp.trim().split(" ");
            try {
                int i = 1;
                for(; i < strings.length; ++i) {
                    if(!strings[i].equals("")) {
                        break;
                    }
                }
                System.out.println(strings[i]);
                return VirtualMachine.attach(strings[i]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
