package com.github.airst.CTools.server;

import com.github.airst.CTools.CommonUtil;
import com.github.airst.CTools.AgentUtil;
import com.github.airst.StaticContext;

import java.io.*;

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
    private static final String OPTION_CLIENT_PATH = "clientPath";
    private static final String OPTION_PARAMETER = "parameter";

    private Request request;
    private Response response;

    public Execute(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public void exec() throws Exception {
        byte[] file = request.getFile("file");
        String option = request.getParameter(OPTION_NAME);
        try {

            if (file != null) {
                if (OPTION_RUN_TEST.equalsIgnoreCase(option)) {
                    String parameter = request.getParameter(OPTION_PARAMETER);
                    String[] p = null;
                    if(parameter != null) {
                        p = parameter.split(",");
                    }
                    doRunTest(file, p);
                } else if (OPTION_SAVE_FILE.equalsIgnoreCase(option)) {
                    String savePath = request.getParameter(OPTION_CLIENT_PATH);
                    doSaveFile(file, savePath);
                } else if (OPTION_HOT_SWAP.equalsIgnoreCase(option)) {
                    doHotSwap(file);
                }

                response.write(option.getBytes());
                response.write(" --> [ok]\r\n".getBytes());
                response.close();
            } else {
                response.write("<form method=\"post\" enctype=\"multipart/form-data\">\r\n".getBytes());
                response.write("file <input type=\"file\" name=\"file\"/><br>".getBytes());
                response.write("option <input type=\"text\" name=\"option\"/>".getBytes());
                response.write("clientPath <input type=\"text\" name=\"clientPath\"/>".getBytes());
                response.write("<input type=\"submit\"/>".getBytes());
                response.write("</form>\r\n".getBytes());
                response.close();
            }

        } catch (Exception e) {
            e.printStackTrace(new PrintStream(response.getOutputStream()));
            response.close();
        }
    }

    private void doRunTest(byte[] data, String[] p) throws Exception {
        AgentUtil.runTestMethod(data, p);
    }

    private void doSaveFile(byte[] data, String savePath) throws Exception {
        String appPath = "/home/admin/" + StaticContext.appName + "/target/" + StaticContext.appName + ".war" ;
        System.out.println(appPath);
        File targetFile = findTargetFile(new File(appPath), savePath);
        if (targetFile != null && createFile(targetFile)) {
            System.out.println(targetFile.getAbsolutePath());
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
        AgentUtil.replaceClassFile(data);
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
