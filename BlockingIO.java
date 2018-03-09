import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO: Make this write a 500kb message instead of just "foo".
 */
public class BlockingIO {

    private static final ExecutorService THREADS = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {

        /* 
         * Read with following command: "nc 127.0.0.1 1888"
         */
        InetAddress address = InetAddress.getByName("127.0.0.1");
        try (final ServerSocket server = new ServerSocket(1888, 1, address)) {
            while (true) {
                final Socket client = server.accept();
                THREADS.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.getOutputStream().write("foo".getBytes());
                            client.close();
                        } catch (final IOException ex) {
                            throw new IllegalArgumentException(ex);
                        }
                    }
                });
            }
        } finally {
            THREADS.shutdown();
        }
    }
}
