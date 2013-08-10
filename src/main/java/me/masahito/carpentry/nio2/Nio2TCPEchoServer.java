package me.masahito.carpentry.nio2;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import me.masahito.carpentry.nio2.handlers.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;


public class Nio2TCPEchoServer extends AbstractExecutionThreadService {
    private final AsynchronousServerSocketChannel server;

    public Nio2TCPEchoServer(final int port) throws IOException {
        this.server = AsynchronousServerSocketChannel.open();
        this.server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        this.server.bind(new InetSocketAddress(port));
    }

    @Override
    protected void run() throws Exception {
        server.accept(this.server, new Acceptor());

        while(isRunning()) {
            Thread.sleep(1000);
        }

    }

    public static void main(String[] args) throws Exception {
        new Nio2TCPEchoServer(9000).start();
    }


}
