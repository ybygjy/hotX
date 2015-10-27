package com.ziqi.fastdev.CTools.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description: Server
 * User: qige
 * Date: 15/7/1
 * Time: 下午1:14
 */
public class Server implements Runnable {

    //端口
    private int port;

    //mltipart/form-data方式提交post的分隔符,
    private String boundary = null;

    //post提交请求的正文的长度
    private int contentLength = 0;

    public Server(int port) {
        this.port = port;
    }

    public void service() throws Exception {
        ServerSocket serverSocket = new ServerSocket(this.port);
        System.out.println("server is ok.");
        //开启serverSocket等待用户请求到来，然后根据请求的类别作处理
        //在这里我只针对GET和POST作了处理
        //其中POST具有解析单个附件的能力
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("new request coming.");

            new Thread(new Worker(socket)).start();
        }
    }

    public static void main(String args[]) throws Exception {
        Server server = new Server(8013);
        server.service();
    }

    @Override
    public void run() {
        try {
            this.service();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
