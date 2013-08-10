package me.masahito.carpentry.nio2.handlers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by test on 13/08/16.
 */


class Writer implements CompletionHandler<Integer, AsynchronousSocketChannel> {

    private ByteBuffer buffer;

    public Writer(ByteBuffer buffer){
        this.buffer = buffer;
    }

    public void completed(Integer result, AsynchronousSocketChannel channel) {
        System.out.println(String.format("write: name: %s", Thread.currentThread().getName()));
        buffer.clear();
        channel.read(buffer, channel, new Reader(buffer));
    }

    public void failed(Throwable exception, AsynchronousSocketChannel channel) {
        throw new RuntimeException(exception);
    }
}
