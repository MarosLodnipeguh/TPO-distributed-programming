/**
 *
 *  @author Szymkowiak Marek S28781
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client_old_selector {

    private String host;
    private int port;
    private String id;

    private SocketChannel socket;
    private Selector selector;

    private StringBuilder log = new StringBuilder();

    public Client_old_selector (String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
//            socket = socket.open();
//            socket.configureBlocking(false);
//            socket.connect(new InetSocketAddress(host, port));
//            selector = Selector.open();
//            socket.register(selector, SelectionKey.OP_CONNECT);
//            socket.finishConnect();
//            while (!socket.finishConnect()) {
//                // Wait for connection to be established
//            }

            socket = socket.open(new InetSocketAddress(host, port));
            socket.configureBlocking(false);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send (String req){
        try{
            if(socket.isOpen()){
                socket.write(ByteBuffer.wrap(req.getBytes(StandardCharsets.UTF_8)));
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                while(byteBuffer.position()==0){
                    socket.read(byteBuffer);
//                    Thread.sleep(100);
                }


                byteBuffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                String resp = charBuffer.toString();

                // log
//                if(resp.equals("logged in"))
//                    log.append("\nlogged in");
//                else if (req.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{4}-\\d{2}-\\d{2}") || req.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}\\s\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")){
//                    log.append("\nRequest: ").append(req).append("\nResult:\n").append(resp);
//                } else if (req.equals("bye") || req.equals("bye and log transfer")){
//                    log.append("\nlogged out\n=== ").append(id).append("log end ===");
//                }

                return resp;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    public String send(String req) {
//        try {
//            // Send request only when server is writable
//            Selector selector = Selector.open();
//            socket.register(selector, SelectionKey.OP_WRITE);
//
//            while (true) {
//                if (selector.select() > 0) {
//                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//                    while (iterator.hasNext()) {
//                        SelectionKey key = iterator.next();
//                        iterator.remove();
//                        if (key.isWritable()) {
//                            // Send request
//                            ByteBuffer requestBuffer = ByteBuffer.wrap(req.getBytes());
//                            socket.write(requestBuffer);
////                            key.cancel(); // We only need to write once, so cancel OP_WRITE
//                            // Set up non-blocking mode for reading
//                            socket.configureBlocking(false);
//                            key.interestOps(SelectionKey.OP_READ); // Now we're interested in reading
//                            // Wait for response
//                            while (true) {
//                                if (selector.select() > 0) {
//                                    iterator = selector.selectedKeys().iterator();
//                                    while (iterator.hasNext()) {
//                                        key = iterator.next();
//                                        iterator.remove();
//                                        if (key.isReadable()) {
//                                            // Read response
//                                            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
//                                            int bytesRead = socket.read(responseBuffer);
//                                            if (bytesRead > 0) {
//                                                responseBuffer.flip();
//                                                String response = new String(responseBuffer.array(), 0, bytesRead).trim();
//                                                return response;
//                                            }
//                                        }
//                                    }
//                                }
//                                // Wait a short time before checking again to avoid busy waiting
//                                Thread.sleep(100);
//                            }
//                        }
//                    }
//                }
//                // Wait a short time before checking again to avoid busy waiting
//                Thread.sleep(100);
//            }
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }



//    public String send (String req) {
//        try {
//            // send request
//            ByteBuffer requestBuffer = ByteBuffer.wrap(req.getBytes());
//            socket.write(requestBuffer);
//            System.out.println("Client sends request: " + req);
//
//            // wait for response
//            while (!socket.finishConnect()) {
//                // Wait for connection to be established
//            }
//
//
//
//            // get response
//            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
//            socket.read(responseBuffer);
//            int bytesRead = responseBuffer.position();
//
//            System.out.println("Client got response: " + bytesRead);
//
//            if (bytesRead != -1) {
//                responseBuffer.flip();
//                String response = new String(responseBuffer.array(), 0, bytesRead).trim();
//                System.out.println("Client got response: " + response);
//                return response;
//            }
//
//            return "Client got no response from server";
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public String send (String req) {
//        // non-blocking I/O
//        try {
//            selector.select();
//            Set<SelectionKey> selectedKeys = selector.selectedKeys();
//            Iterator<SelectionKey> i = selectedKeys.iterator();
//            while (i.hasNext()) {
//                SelectionKey key = i.next();
//                i.remove();
//                if (key.isConnectable()) {
//                    // Connection is established
//                    socket channel = (socket) key.channel();
//                    if (channel.isConnectionPending()) {
//                        channel.finishConnect();
//                    }
//                    // Now the channel is connected, you can start reading/writing
//                    channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//                } else if (key.isWritable()) {
//                    // Write data to the channel
//                    socket channel = (socket) key.channel();
//                    ByteBuffer buffer = ByteBuffer.wrap(req.getBytes());
//                    channel.write(buffer);
//                    System.out.println("Client sends request: " + req);
//                } else if (key.isReadable()) {
//                    // Read data from the channel
//                    processRequest(key);
//                }
//            }
//
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return "no response";
//
//    }

    public String getId () {
        return id;
    }

}
