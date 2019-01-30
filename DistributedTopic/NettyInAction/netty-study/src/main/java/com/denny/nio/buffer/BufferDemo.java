package com.denny.nio.buffer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BufferDemo {

    public static void decode(String str) throws UnsupportedEncodingException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        byteBuffer.put(str.getBytes("UTF-8"));
        byteBuffer.flip();

        /*获取UTF-8的编解码器*/
        Charset charset = Charset.forName("UTF-8");
        CharBuffer charBuffer = charset.decode(byteBuffer);
        char[] charArr = Arrays.copyOf(charBuffer.array(), charBuffer.limit());
        System.out.println(charArr);
    }

    public static void encode(String str){
        CharBuffer charBuffer = CharBuffer.allocate(128);
        charBuffer.append(str);
        charBuffer.flip();//翻转读取

        /*获取UTF-8的编解码器*/
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer byteBuffer = charset.encode(charBuffer);
        byte[] charArr = Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());//编码后的有效长度为0~limit
        System.out.println(charArr);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        BufferDemo.decode("张奎");
        BufferDemo.encode("张奎");
    }
}
