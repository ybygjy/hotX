package com.github.airst.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //解析数据
        Response response = new Response(out, socket);
        Request request = new Request(in, response);

        try {
            request.init();

            //执行服务
            Execute execute = new Execute(request, response);
            execute.exec();
            socket.close();
        } catch (Exception e) {
            if(socket.isClosed() || socket.isOutputShutdown()) {
                e.printStackTrace();
            } else {
                e.printStackTrace(new PrintStream(response.getOutputStream()));
            }
        }

        System.out.println("socket closed.");
    }
}
