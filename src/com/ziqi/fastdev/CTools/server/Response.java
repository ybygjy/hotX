package com.ziqi.fastdev.CTools.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Description: Response
 * User: qige
 * Date: 15/7/1
 * Time: 下午1:14
 */
public class Response {

    private OutputStream out;

    private final String head = "HTTP/1.1 200 OK\n" +
            "Content-Type: text/html;charset=utf-8\n" +
            "Date: " + new Date() + "\n";

    private ByteArrayOutputStream cache = new ByteArrayOutputStream();

    public Response(OutputStream out) {
        this.out = out;
    }

    public void write(byte[] bytes) throws IOException {
        cache.write(bytes);
    }

    public void flush() throws IOException {
        if (out != null) {
            out.write(head.getBytes());
            out.write(("Content-Length: " + cache.size() + "\n\n").getBytes());
            out.write(cache.toByteArray());
        }
    }

    public void close() throws IOException {
        if (out != null) {
            flush();
            cache.close();
            out.close();
        }
    }

    public OutputStream getOutputStream() {
        return cache;
    }

}
