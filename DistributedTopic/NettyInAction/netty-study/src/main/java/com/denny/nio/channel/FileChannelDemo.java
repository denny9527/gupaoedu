package com.denny.nio.channel;

import javax.print.DocFlavor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChannelDemo {

    public static void main(String[] args) {
        try {
            //写入文件
            FileOutputStream os = new FileOutputStream("/Users/denny/Documents/test.txt");
            FileChannel fileChannel = os.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(64);

            byteBuffer.put(new String("hello world").getBytes("UTF-8"));
            byteBuffer.flip();

            fileChannel.write(byteBuffer);

            byteBuffer.clear();

            byteBuffer.put(new String(" dennyzk ok").getBytes("UTF-8"));
            byteBuffer.flip();

            fileChannel.write(byteBuffer);
            byteBuffer.clear();

            fileChannel.close();

            //读取文件
            Path path = Paths.get("/Users/denny/Documents/test.txt");
            FileChannel fileChannel1 = FileChannel.open(path);

            ByteBuffer byteBuffer1 = ByteBuffer.allocate((int)fileChannel1.size() + 1);
            fileChannel1.read(byteBuffer1);

            byteBuffer1.flip();

            Charset charset = Charset.forName("UTF-8");

            CharBuffer charBuffer = charset.decode(byteBuffer1);

            System.out.println(new String(charBuffer.array()));

            byteBuffer1.clear();

            fileChannel1.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
