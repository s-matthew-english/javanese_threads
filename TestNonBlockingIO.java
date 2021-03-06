import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.Test;

public class TestNonBlockingIO {

  @Before
  public void executedBeforeEach() throws Exception {

    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          NonBlockingIO.run();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
    Thread.sleep(1000);
  }

//  @Test
//  public void testIO() throws Exception {
//
//    Socket socket = new Socket("127.0.0.1",1888);
//    InputStream is = socket.getInputStream();
//    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//    int nRead;
//    byte[] data = new byte[1024];
//    while ((nRead = is.read(data, 0, data.length)) != -1) {
//      buffer.write(data, 0, nRead);
//    }
//    buffer.flush();
//
//    String output = new String(buffer.toByteArray());
//    System.out.println(output);
//    socket.close();
//  }

  @Test
  public void testConcurrentIO() throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    List<Thread> threads = new ArrayList<Thread>();
    for (int i = 0; i < 30; i++) {
      Runnable task = new MyRunnable(countDownLatch);
      Thread worker = new Thread(task);
      worker.setName(String.valueOf(i));
      worker.start();
      threads.add(worker);
    }
    int running = 0;
    do {
      running = 0;
      for (Thread thread : threads) {
        if (thread.isAlive()) {
          running++;
        }
      }
      countDownLatch.countDown();
      System.out.println("We have " + running + " running threads. ");
    } while (running > 0);

  }
}

class MyRunnable implements Runnable {
  CountDownLatch countDownLatch;

  MyRunnable(CountDownLatch countDownLatch) {
    this.countDownLatch = countDownLatch;
  }

  @Override
  public void run() {
    try {
      Socket socket = new Socket("127.0.0.1",1888);
      countDownLatch.await();
      InputStream is = socket.getInputStream();
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = is.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      buffer.flush();

      String output = new String(buffer.toByteArray());
      //System.out.println(output);
      socket.close();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
