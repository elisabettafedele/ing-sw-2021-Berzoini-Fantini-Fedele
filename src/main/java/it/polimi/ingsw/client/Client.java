package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.messages.ConnectionMessage;
import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.messages.toClient.NotifyClientDisconnection;
import it.polimi.ingsw.messages.toClient.TimeoutExpiredMessage;
import it.polimi.ingsw.messages.toClient.game.ChooseLeaderCardsRequest;
import it.polimi.ingsw.messages.toClient.lobby.NumberOfPlayersRequest;

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

    private boolean gameCanceled = false;

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
                    closeSocket();
                    break;
                }
            }
        });

    }

    public void start() throws IOException {
        socket = new Socket();
        this.incomingPackets = new LinkedBlockingQueue<>();

        socket.connect(new InetSocketAddress(IPAddress, port), SOCKET_TIMEOUT);
        os = new ObjectOutputStream(socket.getOutputStream());
        is = new ObjectInputStream(socket.getInputStream());



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
                    return;
                }
                else if(message != null && !(message == ConnectionMessage.PING)) {
                    incomingPackets.add(message);
                }
                if (message instanceof NotifyClientDisconnection && ((NotifyClientDisconnection)message).isGameCancelled())
                    gameCanceled = true;
            }
        } catch (IOException | ClassNotFoundException e){
            pinger.interrupt();
        } finally {
            connected.set(false);
            closeSocket();
        }
    }

    @Override
    public void sendMessageToServer(Serializable message){
        if (connected.get() && !gameCanceled){
            try {
                os.writeObject(message);
                os.flush();
            } catch (IOException e) {
                connected.set(false);
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
            //System.out.println(message.toString());
            if (message instanceof ChooseLeaderCardsRequest || message instanceof NumberOfPlayersRequest)
                gameCanceled = false;
            System.out.println(message.toString());
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
        view.handleCloseConnection(wasConnected);
        if (!wasConnected) {
            return;
        } else {
            connected.set(false);
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


    public boolean isConnected() {
        return connected.get();
    }
}
