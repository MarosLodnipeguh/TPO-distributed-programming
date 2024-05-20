/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server_old extends Thread {

    private String host;
    private int port;
    private String log;

    private ServerSocketChannel serverSocket;
    private Selector selector;
    private Map<SocketChannel, String> clogs;
    private Map<SocketChannel, String> clientIds;

    public Server_old (String host, int port) {
        this.host = host;
        this.port = port;
        this.log = "";
        this.clogs = new HashMap<>();
        this.clientIds = new HashMap<>();
    }

    public void startServer() {

        try {
            // create server socket channel and register it with selector
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(host, port));
            serverSocket.configureBlocking(false);

            // create selector
            selector = Selector.open();
//            socket.register(selector, socket.validOps());
            serverSocket.register(selector, SelectionKey.OP_ACCEPT, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        start();

    }

    @Override
    public void run () {
        try {
            while (!interrupted()) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();
                    if (key.isAcceptable()) {
                        // New client has been accepted
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel clientSocket = serverChannel.accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);

                        // add client to logs map with empty log
                        clogs.put(clientSocket, "");
                    }
                    else if (key.isReadable()) {
                        processRequest(key);
                    }


                }
            }
//            Thread.sleep(100); // ??????????
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequest (SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();

        ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
        client.read(requestBuffer);
        String request = new String(requestBuffer.array()).trim();

        if (request.length() > 0) {
            if (request.contains("login")) {
                String clientId = request.split(" ")[1];
                clientIds.put(client, clientId);

                // log server
                log += clientId + " logged in at " + LocalTime.now() + "\n";

                String response = "logged in\n";

                System.out.println("login request from " + clientId);

                // log client
                String clog = clogs.get(client);
                clog += "=== " + clientId + " log start ===\n";
                clog += "logged in\n";
                clogs.put(client, clog);

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                client.write(responseBuffer);
                responseBuffer.clear();
            }
            else if (request.equals("bye")) {
                String clientId = clientIds.get(client);

                // log server
                log += clientId + " logged out at " + LocalTime.now() + "\n";

                System.out.println("logout request from " + clientId);

                // log client
                String clog = clogs.get(client);
                clog += "logged out\n";
                clogs.put(client, clog);

                String response = "logged out";

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                client.write(responseBuffer);
                responseBuffer.clear();

            } else if (request.equals("bye and log transfer")) {
                String clientId = clientIds.get(client);

                // log server
                log += clientId + " logged out at " + LocalTime.now() + "\n";

                System.out.println("logout request from " + clientId);

                // log client
                String clog = clogs.get(client);
                clog += "logged out\n";
                clog += "=== " + clientId + " log end ===\n";
                clogs.put(client, clog);

                String response = clog;

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                client.write(responseBuffer);
                responseBuffer.clear();
            }
            else {
                String clientId = clientIds.get(client);

                // log server
                log += clientId + " request at " + LocalTime.now() + ": \"" + request + "\"\n";

                System.out.println("request from " + clientId + ": " + request);

                String d1 = request.split(" ")[0];
                String d2 = request.split(" ")[1];
                String response = Time.passed(d1, d2);

                // log client
                String clog = clogs.get(client);
                clog += "Request: " + request + "\n";
                clog += "Result:\n" + response + "\n";
                clogs.put(client, clog);

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
                client.write(responseBuffer);
                responseBuffer.clear();
            }


        }
        else {
            System.out.println("empty request");
            client.close();
        }
    }


    public void stopServer() {
        interrupt();
        while (this.isAlive()) {
            // wait for server to stop
        }
        System.out.println("Server stopped");
    }

    String getServerLog() {
        while (this.isAlive()) {
            // wait for server to stop
        }
        if (log.isEmpty()) System.out.println("empty server log");
        return log;
    }


}
