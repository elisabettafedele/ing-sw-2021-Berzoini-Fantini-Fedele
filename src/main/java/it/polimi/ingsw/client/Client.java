package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.messages.ConnectionMessage;
import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.messages.toClient.TimeoutExpiredMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements ClientInterface {
    private final int SOCKET_TIMEOUT = 100000;
    public static final int PING_PERIOD = 5000; //PING_PERIOD = TIMEOUT/2

    private View view;

    private Socket socket;

    private final String IPAddress;
    private final int port;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    private final Thread packetReceiver;
    private final Thread serverObserver;

    private BlockingQueue<Object> incomingPackets;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    private final Thread pinger;

    private boolean connectionClosed = false;

    public Client(String IPAddress, int port, View view) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.view = view;
        this.packetReceiver = new Thread(this::manageIncomingPackets);
        this.serverObserver = new Thread(this::waitForMessages);

        this.pinger = new Thread(() -> {
            while (connected.get()){
                try{
                    Thread.sleep(PING_PERIOD);
                    sendMessageToServer(ConnectionMessage.PING);
                }catch (InterruptedException e){
                    break;
                }
            }
        });

    }

    public void start(){
        socket = new Socket();
        this.incomingPackets = new LinkedBlockingQueue<>();
        try {
            socket.connect(new InetSocketAddress(IPAddress, port), SOCKET_TIMEOUT);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            closeSocket();
            return;
        }

        connected.set(true);
        packetReceiver.start();
        serverObserver.start();

    }

    public void waitForMessages(){
        try {
            while(connected.get()){
                Object message = null;
                message = is.readObject();

                if (message == ConnectionMessage.CONNECTION_CLOSED)
                    closeSocket();
                if (message instanceof TimeoutExpiredMessage){
                    packetReceiver.interrupt();
                    pinger.interrupt();
                    ((TimeoutExpiredMessage) message).handleMessage(view);
                    break;
                }
                else if(message != null && !(message == ConnectionMessage.PING)) {
                    incomingPackets.add(message);
                }

            }
        } catch (IOException | ClassNotFoundException e){
            pinger.interrupt();
        } finally {
            closeSocket();
        }
    }

    @Override
    public void sendMessageToServer(Serializable message){
        if (connected.get()){
            try {
                os.writeObject(message);
                os.flush();
            } catch (IOException e) {
                closeSocket();
            }
        }
    }

    public void manageIncomingPackets(){
        while (connected.get()){
            Object message;
            try {
                message = incomingPackets.take();
            } catch (InterruptedException e) {
                connected.set(false);
                break;
            }
            ((MessageToClient) message).handleMessage(view);
        }
    }

    public void closeSocket() {
        if (connectionClosed)
            return;
        connectionClosed = true;
        boolean wasConnected = connected.get();
        if (packetReceiver.isAlive())
            packetReceiver.interrupt();
        if (!wasConnected) {
            System.out.println(Colour.ANSI_BRIGHT_CYAN.getCode() + "The server is not reachable at the moment. Try again later." + Colour.ANSI_RESET);
            return;
        } else {
            connected.set(false);
            System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "Connection closed" + Colour.ANSI_RESET);
            try {
                is.close();
            } catch (IOException e) {
            }
            try {
                os.close();
            } catch (IOException e) {
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }


}
