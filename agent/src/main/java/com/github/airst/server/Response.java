package com.github.airst.server;

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

    public final String head = "HTTP/1.1 200 OK\n" +
            "Content-Type: text/html;charset=utf-8\n" +
            "Date: " + new Date() + "\n";

    private ByteArrayOutputStream cache = new ByteArrayOutputStream();

    public Response(OutputStream out, Socket socket) {
        this.out = out;
        this.socket = socket;
    }

    public void write(byte[] bytes) throws IOException {
        cache.write(bytes);
    }

    public void writeDirect(byte[] bytes) throws IOException {
        if(needWriteHead) {
            flushHead();
            flushLine();
            needWriteHead = false;
        }
        out.write(bytes);
        out.flush();
    }

    public void writeDirect(byte[] bytes, int offset, int length) throws IOException {
        if(needWriteHead) {
            flushHead();
            flushLine();
            needWriteHead = false;
        }
        out.write(bytes, offset, length);
        out.flush();
    }

    public void flush() throws IOException {
        if (out != null) {
            flushHead();
            flushLength();
            flushLine();
            flushData();
            out.flush();
        }
    }

    public void flushHead() throws IOException {
        if (out != null) {
            out.write(head.getBytes());
            out.flush();
        }
    }

    public void flushLength() throws IOException {
        if (out != null) {
            out.write(("Content-Length: " + cache.size() + "\n").getBytes());
            out.flush();
        }
    }

    public void flushLine() throws IOException {
        if (out != null) {
            out.write(("\n").getBytes());
            out.flush();
        }
    }

    public void flushData() throws IOException {
        if (out != null) {
            out.write(cache.toByteArray());
            cache.reset();
            out.flush();
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

    public boolean isClosed() {
        return socket.isClosed();
    }

}
