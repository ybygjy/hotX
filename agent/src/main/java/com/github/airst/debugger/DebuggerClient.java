package com.github.airst.debugger;

import com.github.airst.server.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description: DebuggerClient
 * Author: qige
 * Date: 16/2/3
 * Time: 下午3:28
 */
public class DebuggerClient {

    private Socket socket = null;
    BufferedInputStream inputStream = null;

    private final String host;
    private final int port;
    private byte[] data1304 = new byte[1304];

    private static DebuggerClient debuggerClient = null;

    private DebuggerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static DebuggerClient getInstance() {
        if (debuggerClient == null) {
            debuggerClient = new DebuggerClient("127.0.0.1", 8000);
        }
        return debuggerClient;
    }

    public void send(byte[] data) {
        try {
            if (data.length == 14 && "JDWP-Handshake".equals(new String(data))) {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                socket = new Socket(host, port);
                inputStream = new BufferedInputStream(socket.getInputStream());
            }
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int byte2int(byte[] res) {
        return (int) res[3] & 0xff | ((int) res[2] & 0xff) << 8 | ((int) res[1] & 0xff) << 16 | ((int) res[0] & 0xff) << 24;
    }

    public byte[] read(int size, Response response) throws Exception {
        System.out.println("1 " + size);
        byte[] head = readData(size);
        System.out.println("1.1 " + size);
        response.writeDirect(head, 0, size);

        int length = byte2int(head) - size;
        System.out.println("2 " + length);
        if (length > 0 && !"JDWP-Handshake".equals(new String(head, 0, 14))) {
            while (length > 1304) {
                byte[] data = readData(1304);
                response.writeDirect(data, 0, 1304);
                length -= 1304;
                System.out.println("3 " + length);
            }
            byte[] data = readData(length);
            System.out.println("4.");
            response.writeDirect(data, 0, length);
            return data;
        } else {
            System.out.println("5.");
            return head;
        }
    }

    private byte[] readData(int size) throws IOException {
        byte[] buffer = data1304;
        int nIdx = 0;
        int nReadLen;

        while (nIdx < size) {
            nReadLen = inputStream.read(buffer, nIdx, size - nIdx);
            if (nReadLen > 0) {
                if (size == 11) {
                    System.out.println("part " + nReadLen);
                }
                nIdx = nIdx + nReadLen;
            } else {
                break;
            }
        }
        return buffer;
    }

    public void dispose() {
        if (socket != null && !socket.isClosed()) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
