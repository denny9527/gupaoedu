package com.denny.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class Client {

    private static final int PORT = 7777;

    private static final String HOST = "127.0.0.1";

    public static void send(String message){
        send(PORT, message);
    }

    private static void send(int port, String message) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter printWriter = null;

        try {
            socket = new Socket(HOST, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            printWriter.println(message);
            log.info("接受到的结果为："+in.readLine());

        }catch (Exception e){
            log.error(e.getLocalizedMessage());

        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            in = null;
            if(printWriter != null) printWriter.close();
            printWriter = null;

            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket = null;
        }
    }
}
