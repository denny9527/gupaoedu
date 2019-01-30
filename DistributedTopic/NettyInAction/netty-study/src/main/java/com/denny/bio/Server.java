package com.denny.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {

    public static final int PORT = 7777;

    private static ServerSocket serverSocket;

    public static void start(){
        start(PORT);
    }

    private synchronized static void start(int port) {
            if(serverSocket != null){
                return;
            }
            try {
                serverSocket = new ServerSocket(port);
                log.info("服务端已启动，端口号："+port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    log.info("服务端接受到客户端请求！");
                    new Thread(new ServerHandler(socket)).start();
                }
            }catch (Exception e){
                log.error(e.getLocalizedMessage());
            }finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    serverSocket = null;
                }
            }

    }

}
