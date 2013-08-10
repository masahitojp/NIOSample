package me.masahito.carpentry;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import java.util.concurrent.ExecutionException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Nio2TCPEchoServer extends AbstractExecutionThreadService {


    private static final int TIMEOUT = 10;
    private static final int BUFFER_SIZE = 8192;
    private final AsynchronousServerSocketChannel server;

    public Nio2TCPEchoServer(final int port) throws IOException {
        this.server = AsynchronousServerSocketChannel.open();
        this.server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        this.server.bind(new InetSocketAddress(port));
    }

    @Override
    protected void run() throws Exception {
        server.accept(this.server, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel serverChannel) {
                try {
                    doEcho(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server.accept(serverChannel, this);
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {

            }
        });

        while(isRunning()) {
            Thread.sleep(1000);
        }

    }

    private void doEcho(final AsynchronousSocketChannel channel) throws IOException {
        try (final AsynchronousSocketChannel acceptedChannel = channel) {

            final ByteBuffer buff = ByteBuffer.allocateDirect(BUFFER_SIZE);

            acceptedChannel.read(buff).get(TIMEOUT, TimeUnit.SECONDS);

            buff.flip();
            final byte[] bytes = new byte[buff.limit()];
            buff.get(bytes);
            buff.compact();
            buff.clear();
            buff.put(bytes);
            buff.flip();

            acceptedChannel.write(buff).get(TIMEOUT, TimeUnit.SECONDS);

        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        new Nio2TCPEchoServer(9000).start();
    }


}
