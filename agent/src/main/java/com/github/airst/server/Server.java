package com.github.airst.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Description: Server
 * User: qige
 * Date: 15/7/1
 * Time: 下午1:14
 */
public class Server {

    //端口
    private int port;

    private ServerSocket serverSocket = null;

    public Server(int port) {
        this.port = port;
    }

    public void service() throws Exception {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("new request coming.");

                new Thread(new Worker(socket)).start();
            }
        } catch (SocketException e) {
            System.out.println("server closed.");
            e.printStackTrace(System.out);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void bind() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(this.port);
        }
    }

    public void shutdown() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
