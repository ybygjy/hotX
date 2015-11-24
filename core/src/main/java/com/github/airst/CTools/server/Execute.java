package com.github.airst.CTools.server;

import com.github.airst.CTools.AgentUtil;
import com.github.airst.CTools.StringUtil;
import com.github.airst.HotXBoot;
import com.github.airst.StaticContext;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: Execute
 * User: qige
 * Date: 15/7/1
 * Time: 下午2:13
 */
public class Execute {

    private static final String OPTION_NAME = "option";
    private static final String OPTION_RUN_TEST = "runTest";
    private static final String OPTION_SAVE_FILE = "saveFile";
    private static final String OPTION_HOT_SWAP = "hotSwap";
    private static final String OPTION_SHUTDOWN = "shutdown";
    private static final String OPTION_PARAMETER = "parameter";

    private static final String MAIN_FILE = "mainFile";

    private Request request;
    private Response response;

    public Execute(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public void exec() throws Exception {
        Map<String, byte[]> fileMap = request.getFiles();
        String option = request.getParameter(OPTION_NAME);
        try {

            String msg = "";
            String res = "ok";
            if (OPTION_RUN_TEST.equalsIgnoreCase(option)) {
                String parameter = request.getParameter(OPTION_PARAMETER);
                String[] p = null;
                if(parameter != null) {
                    p = parameter.split(",");
                }
                List<byte[]> sub = new ArrayList<byte[]>();
                for(Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                    if(!entry.getKey().equals(MAIN_FILE)) {
                        sub.add(entry.getValue());
                        msg += "get sub class " + request.getParameter(entry.getKey()) + "\r\n";
                    }
                }
                doRunTest(request.getFile(MAIN_FILE), p, sub);
                msg += "runTest " + request.getParameter(MAIN_FILE) + "\r\n";
            } else if (OPTION_SAVE_FILE.equalsIgnoreCase(option)) {
                if(!StringUtil.isBlank(StaticContext.getAppName()) && !StringUtil.isNullStr(StaticContext.getAppName())) {
                    String savePath = request.getParameter(MAIN_FILE);
                    doSaveFile(request.getFile(MAIN_FILE), savePath);
                } else {
                    msg += "please use hotX.sh [pid] [appName] to start hotX for saveFile function" + "\r\n";
                    res = "failed";
                }
            } else if (OPTION_HOT_SWAP.equalsIgnoreCase(option)) {
                for(Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                    doHotSwap(entry.getValue());
                    msg += "hotSwap " + request.getParameter(entry.getKey()) + "\r\n";
                }
            } else if(OPTION_SHUTDOWN.equalsIgnoreCase(option)) {
                doShutdown();
            } else {
                response.write("<form method=\"post\" enctype=\"multipart/form-data\">\r\n".getBytes());
                response.write("file <input type=\"file\" name=\"file\"/><br>".getBytes());
                response.write("option <input type=\"text\" name=\"option\"/>".getBytes());
                response.write("clientPath <input type=\"text\" name=\"clientPath\"/>".getBytes());
                response.write("<input type=\"submit\"/>".getBytes());
                response.write("</form>\r\n".getBytes());
                response.close();
                return;
            }

            response.write(msg.getBytes());
            response.write(option.getBytes());
            response.write((" --> [" + res + "]\r\n").getBytes());
            response.close();

        } catch (Exception e) {
            e.printStackTrace(new PrintStream(response.getOutputStream()));
            response.close();
        }
    }

    private void doShutdown() throws Exception {
        HotXBoot.shutdown();
    }

    private void doRunTest(byte[] data, String[] p, List<byte[]> subClasses) throws Exception {
        AgentUtil.runTestMethod(data, p, subClasses);
    }

    private void doSaveFile(byte[] data, String savePath) throws Exception {
        String appPath = "/home/admin/" + StaticContext.getAppName() + "/target/" + StaticContext.getAppName() + ".war" ;
        response.write((appPath + "\r\n").getBytes());
        File targetFile = findTargetFile(new File(appPath), savePath);
        if (targetFile != null && createFile(targetFile)) {
            response.write((targetFile.getAbsolutePath()  + "\r\n").getBytes());
            FileOutputStream writer = new FileOutputStream(targetFile);
            writer.write(data);
            writer.close();
        }
    }

    private File findTargetFile(File dir, String targetPath) throws Exception {
        if (targetPath.contains("src/main")) {
            String path1 = targetPath.substring(targetPath.indexOf("src/main/") + "src/main/".length());
            String[] items = path1.split("/");
            if (items[0].startsWith("web")) {
                String path = targetPath.substring(targetPath.indexOf(items[0]) + items[0].length());
                return new File(dir.getAbsolutePath() + "/" + path);
            }
        }
        return null;
    }

    private void doHotSwap(byte[] data) throws Exception {
        if(data != null) {
            AgentUtil.replaceClassFile(data);
        }
    }

    private boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            String parent = file.getParent();
            File p = new File(parent);
            return (p.exists() || p.mkdirs()) && file.createNewFile();
        } else {
            return true;
        }
    }

}
