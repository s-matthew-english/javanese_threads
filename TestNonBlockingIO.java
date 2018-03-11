import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TestNonBlockingIO {

  @Before
  public void executedBeforeEach() throws Exception {

    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          BlockingIO.run();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    t.start();
  }

  @Test
  public void testNonBlockingIO() throws Exception {

    Socket socket = new Socket("127.0.0.1",1888);
    InputStream is = socket.getInputStream();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[1024];
    while ((nRead = is.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    buffer.flush();

    String output = new String(buffer.toByteArray());
    System.out.println(output);
    socket.close();
  }
}
