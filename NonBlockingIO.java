import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * TODO: Make this write a 500kb message to each client in the same way the BlockingIO does it.
 * TODO: Write a JUnit test that verifies that 100 clients connecting in parallel all get the full
 * message written back to them.
 */
public class NonBlockingIO {

    public static void main(String[] args) throws Exception {
        try (final ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(1888));
            server.configureBlocking(false);
            final Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select() > 0) {
                    final Set<SelectionKey> keys = selector.selectedKeys();
                    final Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        final SelectionKey key = iterator.next();
                        iterator.remove();
                        final SocketChannel client = ((ServerSocketChannel)key.channel()).accept();
                    }
                }
            }
            //client.write(ByteBuffer.wrap("foo".getBytes()));
            //client.close();
        }
    }
}
