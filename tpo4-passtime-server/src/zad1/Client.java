/**
 * @author Szymkowiak Marek S28781
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {

    private String host;
    private int port;
    private String id;
    private SocketChannel clientSocket;

    public Client (String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect () {
        try {
            clientSocket = clientSocket.open(new InetSocketAddress(host, port));
            clientSocket.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send (String req) {
        try {
            // Send request
            clientSocket.write(StandardCharsets.UTF_8.encode(req));

            // Read response
            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);

            // loop until response is read (position 0 means no data)
            while (responseBuffer.position() == 0) {
                clientSocket.read(responseBuffer);
            }

            responseBuffer.flip();
            String resp = StandardCharsets.UTF_8.decode(responseBuffer).toString();
            return resp;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "no response from server";
    }

    public String getId () {
        return id;
    }

}
