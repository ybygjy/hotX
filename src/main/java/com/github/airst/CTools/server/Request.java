package com.github.airst.CTools.server;

import com.github.airst.CTools.CommonUtil;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Request
 * User: qige
 * Date: 15/7/1
 * Time: 下午1:14
 */
public class Request {

    private InputStream in;

    private Map<String, String> parameters = new HashMap<String, String>();

    private Map<String, byte[]> files = new HashMap<String, byte[]>();

    private String boundary = "";
    private int contentLength = 0;

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addParameter(String name, String value) {
        parameters.put(name, value);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, byte[]> getFiles() {
        return files;
    }

    public void addFile(String name, byte[] fileData) {
        files.put(name, fileData);
    }

    public byte[] getFile(String name) {
        return files.get(name);
    }

    public Request(InputStream in) {
        this.in = in;
    }

    public void init() throws Exception {
        String boundary = "";
        long startTimeMillis = System.currentTimeMillis();
        DataInputStream reader =  new DataInputStream(in);

        readHead(reader);
        readBody(reader);

        long endTimeMillis = System.currentTimeMillis();
        System.out.println("costs: " + (endTimeMillis - startTimeMillis));
    }

    private void readHead(DataInputStream reader) throws Exception {
        String line;
        while((line = reader.readLine()) != null) {
            System.out.println(line);
            if(line.contains("Content-Length")) {
                contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16));
            }
            if(line.contains("multipart/form-data")) {
                boundary = line.substring(line.indexOf("boundary") + 9);
            }
            if(line.equals("")) {
                break;
            }
        }
    }

    private void readBody(DataInputStream reader) throws Exception {
        byte[] bytes = CommonUtil.readStream(reader, contentLength);
        System.out.write(bytes);
        boolean first = true;
        boolean binary = false;
        boolean flag = false;
        int streamStart = 0;
        int streamEnd = 0;
        String name = "";
        for(int i=0, j=0; i < contentLength && j < contentLength; ) {
            if(bytes[j]=='\r' && j+1<contentLength && bytes[j+1]=='\n') {
                j += 2;
                byte[] bLine = new byte[j - i];
                System.arraycopy(bytes, i, bLine, 0, j - i);
                String line = new String(bLine);
//                System.out.print(line);
                if(first) {
                    //排除第一个boundary
                    if(line.contains(boundary)) {
                        first = false;
                    }
                } else if(line.startsWith("Content-Disposition: form-data")) {
                    int start = line.indexOf("name=\"") + "name=\"".length();
                    int end = line.indexOf("\"", start);
                    name = line.substring(start, end);
                    flag = true;
                } else if(line.startsWith("Content-Type: application/octet-stream")) {
                    binary = true;
                    flag = true;
                }else if(streamStart ==0 && line.equals("\r\n") && flag) {
                    streamStart = j;
                } else if(line.contains(boundary)) {
                    streamEnd = i - 2;
                    byte[] data = Arrays.copyOfRange(bytes, streamStart, streamEnd);
                    if(binary) {
                        files.put(name, data);
                    } else {
                        parameters.put(name, new String(data));
                    }
                    binary = false;
                    streamStart = 0;
                } else {
                    flag = false;
                }
                i = j;
            } else {
                ++j;
            }
        }

    }


}
