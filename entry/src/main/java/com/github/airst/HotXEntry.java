package com.github.airst;

import com.google.common.io.ByteStreams;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Description: HotXEntry
 * Author: qige
 * Date: 16/4/29
 * Time: 下午5:24
 */
public class HotXEntry extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        byte[] bytes = ByteStreams.toByteArray(request.getInputStream());

        String targetHost = request.getHeader("targetHost");
        String account = request.getHeader("account");
        String password = request.getHeader("password");

        PrintWriter writer = new PrintWriter(response.getOutputStream());
        RemoteExecuteCommand executeCommand = RemoteExecuteCommand.create(targetHost, account, password);
        if (!executeCommand.login()) {
            writer.println("error: login failed!");
            writer.flush();
            writer.close();
            return;
        }
        String execute;
        execute = executeCommand.execute("netstat -tnl | grep 8080");
        if(StringUtils.isBlank(execute)) {
            execute = executeCommand.execute("curl -sLk http://hotx.oss-cn-hangzhou-zmf.aliyuncs.com/install.sh | sh");
            writer.println(execute);
            executeCommand.execute("echo " + password + " | sudo -S su admin");
            execute = executeCommand.execute("./hotX.sh");
            writer.println(execute);
            writer.flush();
        }

        URL url = new URL("http://" + targetHost + ":8080");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setReadTimeout(0);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", request.getHeader("Content-Type"));

        conn.getOutputStream().write(bytes);
        conn.getOutputStream().flush();

        ByteStreams.copy(conn.getInputStream(), response.getOutputStream());
        response.getOutputStream().flush();
    }

}
