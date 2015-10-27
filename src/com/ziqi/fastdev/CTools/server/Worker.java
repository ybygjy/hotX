package com.ziqi.fastdev.CTools.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Description: Worker
 * User: qige
 * Date: 15/7/2
 * Time: 上午10:50
 */
public class Worker implements Runnable {

    private Socket socket;

    public Worker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            //解析数据
            Request request = new Request(in);
            request.init();
            //执行服务
            Response response = new Response(out);

            Execute execute = new Execute(request, response);
            execute.exec();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("socket closed.");
    }
}
