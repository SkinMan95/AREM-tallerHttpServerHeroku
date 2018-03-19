
import edu.escuelaing.arem.taller.httpserverheroku.HttpServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public class ConcurrentTest {

    public ConcurrentTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        (new ExecutionThread()).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConcurrentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void tearDown() {
        HttpServer.finished = true;
    }

    @Test
    public void test1() throws Exception {
        String ipAddress = "127.0.0.1";
        int port = HttpServer.port;

        boolean pass = true;

        try {
            List<Thread> threads = new ArrayList<>();
            List<AtomicBoolean> bools = new ArrayList<>();

            for (String in : Arrays.asList("/", "/css/styles.css", "/images/invie2x.png")) {
                AtomicBoolean bool = new AtomicBoolean();
                threads.add(new TestThread(in, ipAddress, port, bool));
                bools.add(bool);
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            for (AtomicBoolean bool : bools) {
                System.out.println("pass: " + pass);
                pass &= bool.get();
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());

            pass = false;
        }

        assertTrue(pass);
    }

    private class TestThread extends Thread {

        private final String route;
        private final String ipAddress;
        private final int port;
        private final AtomicBoolean finalState;

        public TestThread(String route, String ipaddress, int port, AtomicBoolean finalState) {
            this.route = route;
            this.ipAddress = ipaddress;
            this.port = port;
            this.finalState = finalState;
        }

        @Override
        public void run() {
            Socket serverSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;

            try {
                serverSocket = new Socket(ipAddress, port);
                out = new PrintWriter(serverSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

                out.println("GET " + route + " HTTP/1.1");
                String response = in.readLine();
                System.out.println("response: " + response);
                if (response.equals("") || response == null) {
                    finalState.set(false);
                } else {
                    finalState.set(true);
                }

                System.out.println("Response for " + route + ": " + response);
            } catch (Exception ex) {
                System.err.println(ex.toString());
                finalState.set(false);
            }

            try {
                serverSocket.close();
                in.close();
                out.close();
            } catch (Exception ex) {

            }
        }
    }

    private class ExecutionThread extends Thread {

        @Override
        public void run() {
            try {
                HttpServer.main(new String[]{});
            } catch (Exception ex) {
                System.err.println(ex.toString());
            }
        }
    }
}
