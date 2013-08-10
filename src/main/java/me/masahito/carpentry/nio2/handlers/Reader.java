package me.masahito.carpentry.nio2.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by test on 13/08/16.
 */
class Reader implements CompletionHandler<Integer, AsynchronousSocketChannel> {

    private ByteBuffer buffer;

    public Reader(ByteBuffer buffer){
        this.buffer = buffer;
    }

    public void completed(Integer result, AsynchronousSocketChannel channel){
        System.out.println(String.format("read: name: %s", Thread.currentThread().getName()));
        if(result != null && result < 0){
            try{
                channel.close();
                return;
            }catch(IOException ignore){}
        }
        buffer.flip();
        channel.write(buffer, channel, new Writer(buffer));
    }
    public void failed(Throwable exception, AsynchronousSocketChannel channel){
        throw new RuntimeException(exception);
    }
}
