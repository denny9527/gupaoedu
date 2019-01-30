package com.denny.nio.buffer;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class Buffers {

    private ByteBuffer readBuffer;

    private ByteBuffer writeBuffer;

    public Buffers(int readCapacity, int writeCapacity){
        readBuffer = ByteBuffer.allocate(readCapacity);
        writeBuffer = ByteBuffer.allocate(writeCapacity);
    }

    public ByteBuffer getReadBuffer(){
        return readBuffer;
    }

    public ByteBuffer getWriteBuffer(){
        return writeBuffer;
    }
}
