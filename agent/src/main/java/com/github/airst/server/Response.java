package com.github.airst.server;

import com.github.airst.debugger.DebuggerClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Description: Response
 * User: qige
 * Date: 15/7/1
 * Time: 下午1:14
 */
public class Response {

    private OutputStream out;

    private Socket socket;

    private boolean needWriteHead = true;

    private boolean chunked = false;

    private static String LINE = "\r\n";

    public final String head = "HTTP/1.1 200 OK" + LINE +
            "Content-Type: text/html;charset=utf-8" + LINE +
            "Date: " + new Date() + "" + LINE;

    private ByteArrayOutputStream cache = new ByteArrayOutputStream();

    public Response(OutputStream out, Socket socket) {
        this.out = out;
        this.socket = socket;
    }

    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int offset, int length) throws IOException {
        if(!chunked) {
            cache.write(bytes, offset, length);
        } else {
            cache.write(getHeader(length));
            cache.write(bytes, 0, length);
            cache.write(LINE.getBytes());
        }
    }

    public void writeEnd() throws IOException {
        out.write("0\r\n".getBytes());
        out.write(LINE.getBytes());
        out.flush();
    }

    private static byte[] getHeader(int size){
        try {
            String hexStr =  Integer.toHexString(size);
            byte[] hexBytes = hexStr.getBytes("US-ASCII");
            byte[] header = new byte[hexBytes.length + 2];
            for (int i=0; i<hexBytes.length; i++) {
                header[i] = hexBytes[i];
                System.out.print((char)hexBytes[i]);
            }
            System.out.println();
            header[hexBytes.length] = '\r';
            header[hexBytes.length+1] = '\n';
            return header;
        } catch (java.io.UnsupportedEncodingException e) {
            /* This should never happen */
            throw new InternalError(e.getMessage());
        }
    }


    public static void main(String[] args) {
        System.out.println(Integer.toHexString(256));
    }

    public void flush() throws IOException {
        if (out != null) {
            if(needWriteHead) {
                if (chunked) {
                    out.write(head.getBytes());
                    out.write(("Transfer-Encoding: chunked" + LINE + LINE).getBytes());
                    needWriteHead = false;
                } else {
                    out.write(head.getBytes());
                    out.write(("Content-Length: " + cache.size() + LINE + LINE).getBytes());
                    needWriteHead = false;
                }
                flushData();
            } else if(chunked) {
                flushData();
            }
            out.flush();
        }
    }

    private void flushData() throws IOException {
        out.write(cache.toByteArray());
        cache.reset();
    }

    public void close() throws IOException {
        if (out != null) {
            flush();
            if(chunked) {
                writeEnd();
            }
            cache.close();
            out.close();
        }
    }

    public OutputStream getOutputStream() {
        return cache;
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public Response chunk() {
        this.chunked = true;
        return this;
    }
}
