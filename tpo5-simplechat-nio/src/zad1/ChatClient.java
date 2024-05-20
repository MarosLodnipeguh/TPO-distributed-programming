/**
 * @author Szymkowiak Marek S28781
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatClient extends Thread {

    private String host;
    private int port;
    private String id;
    private SocketChannel clientSocket;
    private StringBuilder chatView;

    private final Lock lock = new ReentrantLock();

    public ChatClient (String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.chatView = new StringBuilder("=== " + id + " chat view\n");
    }


    public void login () {
        // connect to the server and send login request
        try {
            clientSocket = clientSocket.open(new InetSocketAddress(host, port));
            clientSocket.configureBlocking(false);
            send("login " + id);
        } catch (IOException e) {
            chatView.append("*** " + e + "\n");
        }

        // start reading messages from the server
        this.start();
    }

    public void logout () {
        send("logout " + id);

        // wait to receive the response before closing the socket
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.interrupt();

        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Send request to the server
    public void send (String req) {
        try {
            lock.lock();
            clientSocket.write(StandardCharsets.UTF_8.encode(req + "~"));
//            Thread.sleep(10);
        } catch (Exception e) {
            chatView.append("*** " + e + "\n");
        } finally {
            lock.unlock();
        }
    }

    // listen for messages from the server in loop
    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while (!this.isInterrupted()) {
            try {
                lock.lock();
                int bytesRead = clientSocket.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    String response = StandardCharsets.UTF_8.decode(buffer).toString();
                    chatView.append(response);
                    buffer.clear();
                }
            } catch (IOException e) {
                chatView.append("*** " + e + "\n");
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    public String getChatView () {
        return chatView.toString();
    }
}
