package com.github.airst.server;

import com.github.airst.debugger.DebuggerClient;

import java.util.Map;

/**
 * Description: Execute
 * User: qige
 * Date: 15/7/1
 * Time: 下午2:13
 */
public class Execute {

    private static final String OPTION_NAME = "option";
    private static final String OPTION_DEBUG = "debug";

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

            System.out.println(option);
            if(OPTION_DEBUG.equalsIgnoreCase(option)) {
                doDebug(fileMap.get(MAIN_FILE));
                response.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.close();
        }
    }

    private void doDebug(byte[] data) throws Exception {
        System.out.println(data.length);
        if(data.length == 14 && "JDWP-Handshake".equals(new String(data))) {
            System.out.println("first");
            response.chunk();
            DebuggerClient.getInstance().send(data);
            DebuggerClient.getInstance().read(14, response);
            while (!response.isClosed()) {
                DebuggerClient.getInstance().read(11, response);
            }
            System.out.println("first failed");
        } else {
            DebuggerClient.getInstance().send(data);
        }
    }

}
