import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO: Make this write a 500kb message to each client in the same way the BlockingIO does it.
 * TODO: Write a JUnit test that verifies that 100 clients connecting in parallel all get the full
 * message written back to them.
 */
public class NonBlockingIO {

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {

        /* ServerSocketChannel: A selectable channel for stream-oriented listening sockets.
         *
         * A server-socket channel is created by invoking the `open` method of this class. It is not possible
         * to create a channel for arbitrary, pre-existing ServerSocket. A newly-created server-socket channel
         * is open but not yet bound. An attempt to invoke the `accept` method of an unbound server-socket channel
         * will cause a NotYetBoundException to be thrown. A server-socket channel can be bound by invoking one of the
         * `bind` methods defined by this class.
         */
        try (final ServerSocketChannel server = ServerSocketChannel.open()) {
            /* InetAddress: This class represents an Internet Protocol (IP) address.
             *
             * An IP address is either a 32-bit or 128-bit unsigned number used by IP, a lower-level protocol on which protocols like UDP and TCP are built.
             * The IP address architecture is defined by RFC 790: `Assigned Numbers`. An instance of an InetAddress consists of an IP address and possibly
             * its corresponding host name (depending on whether it is constructed with a host name or whether it has already done reverse host name resolution).
             */
            InetAddress address = InetAddress.getByName("127.0.0.1"); // getByName(String host), Determines the IP address of a host, given the host's name.
            server.bind(new InetSocketAddress(address,1888));
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
                        if(key.isAcceptable()) {
                            final SocketChannel client = ((ServerSocketChannel)key.channel()).accept();

                            byte[] byteArray = new byte[500000];
                            new SecureRandom().nextBytes(byteArray);

                            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);

                            // Returns int
                            client.write(byteBuffer);
                            // call to .remaining returns same information
                            if(byteBuffer.remaining() > 0) {
                                SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
                                clientKey.attach(byteBuffer);
                            }
                        } else if(key.isWritable()){
                            // Get original ByteBuffer back.
                            ByteBuffer byteBuffer = (ByteBuffer)key.attachment();

                            // Returns int
                            SocketChannel client = (SocketChannel)key.channel();
                            client.write(byteBuffer);

                            // call to .remaining returns same information
                            if(byteBuffer.remaining() == 0) {
                                key.cancel();
                                client.close();
                            }
                        }
                        //
                    }
                }
            }
        }
    }
}
