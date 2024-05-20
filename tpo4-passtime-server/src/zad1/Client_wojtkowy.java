/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client_wojtkowy {

    private String host;
    private int port;
    private String id;
    private SocketChannel socket;
    public boolean connected;

    public Client_wojtkowy(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            socket = SocketChannel.open();
            socket.connect(new InetSocketAddress(host, port));
            socket.configureBlocking(false);
//            System.out.println("client " + id + " connected to server");
            connected = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(String req) {
        try {
            // Send request
            socket.write(Charset.defaultCharset().encode(req));

            // read response
            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
            int bytesRead = socket.read(responseBuffer);
            if (bytesRead != -1) {
                responseBuffer.flip();
                return Charset.defaultCharset().decode(responseBuffer).toString();
            }

            return "Client got no response from server";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getId () {
        return id;
    }

}
