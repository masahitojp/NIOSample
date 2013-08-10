package me.masahito.carpentry.nio;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by test on 13/08/10.
 */
public class NioTCPEchoServer extends AbstractExecutionThreadService {

    private static final int TIMEOUT = 10;
    private static final int BUFFER_SIZE = 8192;
    private final ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public NioTCPEchoServer(final int port) throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
    }

    @Override
    protected void run() throws Exception {
        this.selector = Selector.open();
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select(TIMEOUT * 1000) > 0) {
            for (final Iterator it = selector.selectedKeys().iterator(); it.hasNext();) {
                final SelectionKey key = (SelectionKey) it.next();
                it.remove();
                if (key.isAcceptable()) {
                    doAccept((ServerSocketChannel) key.channel());
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    doRead(channel);
                }
            }
        }
    }

    private void doAccept(final ServerSocketChannel serverChannel) {
        try {
            final SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void doRead(final SocketChannel channel) {
        final ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        final Charset charset = Charset.forName("UTF-8");
        try {
            if (channel.read(buf) < 0) {
                return;
            }
            buf.flip();
            System.out.print(
                    charset.decode(buf).toString());
            buf.flip();
            channel.write(buf);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new NioTCPEchoServer(9000).start();
    }


}
