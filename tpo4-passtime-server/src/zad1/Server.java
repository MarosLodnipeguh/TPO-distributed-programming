package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {

    private String host;
    private int port;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private StringBuilder serverLog = new StringBuilder();
    private Map<SocketChannel, String> clientLogs;
    private Map<SocketChannel, String> clientIds;

    public Server (String host, int port) {
        this.host = host;
        this.port = port;
        this.clientLogs = new ConcurrentHashMap<>();
        this.clientIds = new ConcurrentHashMap<>();

        // initialize server socket and selector
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.socket().bind(new InetSocketAddress(host, port));
            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer () {
        start();
    }

    @Override
    public void run () {

        while (!this.isInterrupted()) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keysIterator = keys.iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientSocket = serverSocket.accept();
                        clientSocket.configureBlocking(false);
                        clientSocket.register(selector, SelectionKey.OP_READ);

                        // add client to logs map with empty log
                        clientLogs.put(clientSocket, "");
                    } else if (key.isReadable()) {
                        processRequest(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // close selector after stopping the server
        try {
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequest (SelectionKey key) throws IOException {
        // Get the client socket channel from key
        SocketChannel client = (SocketChannel) key.channel();

        // Read the request
        ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
        client.read(requestBuffer);
        requestBuffer.flip();
        String request = StandardCharsets.UTF_8.decode(requestBuffer).toString();

        if (request.startsWith("login")) {
            // register client id
            String clientId = request.split(" ")[1];
            clientIds.put(client, clientId);
            // log server
            logServer(clientId + " logged in at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
            // log client
            logClient(client, "=== " + clientId + " log start ===");
            logClient(client, "logged in");
            // send response
            sendResponse(client, "logged in");
        } else if (request.equals("bye")) {
            // get client id
            String clientId = clientIds.get(client);
            // log server
            logServer(clientId + " logged out at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
            // log client
            logClient(client, "logged out");
            // send response
            sendResponse(client, "logged out");
            // logout client
            logoutClient(client);
        } else if (request.equals("bye and log transfer")) {
            // get client id
            String clientId = clientIds.get(client);
            // log server
            logServer(clientId + " logged out at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
            // log client
            logClient(client, "logged out");
            logClient(client, "=== " + clientId + " log end ===");
            // send response
            String response = clientLogs.get(client);
            sendResponse(client, response);
            // logout client
            logoutClient(client);
        } else if (request.matches("(\\d{4}(-\\d{2}){2}(T\\d{2}:\\d{2})?\\s?){2}") || request.matches("(\\d{2}:\\d{2}\\s?){2}")) {
            // get client id
            String clientId = clientIds.get(client);
            // calculate response
            String[] parts = request.split(" ");
            String d1 = parts[0];
            String d2 = parts[1];
            String response = Time.passed(d1, d2);
            // log server
            logServer(clientId + " request at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + ": \"" + request + "\"");
            // log client
            logClient(client, "Request: " + request);
            logClient(client, "Result:\n" + response);
            // send response
            sendResponse(client, response);
        }
        else {
            // send response
            sendResponse(client, "bad request");
        }

    }

    public void sendResponse(SocketChannel socketChannel, String resp){
        ByteBuffer byteBuffer = ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8));

        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopServer () {
        interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void logServer (String message) {
        synchronized (serverLog) {
            serverLog.append(message).append("\n");
        }
    }

    public void logClient (SocketChannel client, String message) {
        synchronized (clientLogs) {
            clientLogs.put(client, clientLogs.get(client) + message + "\n");
        }
    }

    private void logoutClient(SocketChannel channel) {
        if (channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getServerLog () {
        return serverLog.toString();
    }
}
