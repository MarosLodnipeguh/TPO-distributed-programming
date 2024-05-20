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
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServer extends Thread {

    private String host;
    private int port;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private StringBuilder serverLog;
    private Map<SocketChannel, String> clients;
    private final Lock lock = new ReentrantLock();

    public ChatServer (String host, int port) {
        this.host = host;
        this.port = port;
        this.serverLog = new StringBuilder();
        this.clients = new ConcurrentHashMap<>();

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
        this.start();
        System.out.println("Server started");
    }

    @Override
    public void run() {
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
                    } else if (key.isReadable()) {
                        processRequest(key);
                    }
                }
            } catch (IOException e) {
                broadcastMessage("*** " + e + "\n");
            }
        }

        // close selector after stopping the server
        try {
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequest(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
        StringBuilder requestBuilder = new StringBuilder();

        int bytesRead;
        try {
            while ((bytesRead = client.read(requestBuffer)) > 0) {
                requestBuffer.flip();
                requestBuilder.append(StandardCharsets.UTF_8.decode(requestBuffer));
                requestBuffer.clear();
            }

            // if client disconnected remove it from clients map and close the connection
            if (bytesRead == -1) {
                clients.remove(client);
                client.close();
                return;
            }

            // handle partial reads
            String[] messageParts = requestBuilder.toString().split("~");

            for (String req : messageParts) {
                String response = handleRequest(req, client);
                broadcastMessage(response);
            }

        } catch (IOException e) {
            logServer("*** " + e + "\n");
        }
    }


    private String handleRequest (String request, SocketChannel client) {
        String response = "";
        if (request.startsWith("login")) {
            // Add client to clients map with id
            String id = request.split(" ")[1];
            clients.put(client, id);

            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            logServer(time + " " + id + " logged in");

            response =  id + " logged in" + "\n";
        } else if (request.equals("logout")) {
            String id = clients.get(client);

            // log server
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            logServer(time + " " + id + " logged out");

            response =  id + " logged out" + "\n";
            clients.remove(client);
        } else {
            String id = clients.get(client);

            // log server
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            logServer(time + " " + id + ": " + request);

            response = id + ": " + request + "\n";
        }
        return response;
    }

    // Broadcast message to all connected clients
    private void broadcastMessage (String message) {
        for (SocketChannel client : clients.keySet()) {
            try {
//                ByteBuffer buffer = StandardCharsets.UTF_8.encode(message);
//                client.write(buffer);
                client.write(StandardCharsets.UTF_8.encode(message));
            } catch (IOException e) {
                logServer("*** " + e + "\n");
            }
        }
    }

    public void stopServer () {
        this.interrupt();
        System.out.println("Server stopped");
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void logServer (String message) {
        serverLog.append(message).append("\n");
//        synchronized (serverLog) {
//            serverLog.append(message).append("\n");
//        }
    }

    public String getServerLog() {
        return serverLog.toString();
    }



}
