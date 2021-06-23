package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.ConnectionMessage;
import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.messages.toClient.NotifyClientDisconnection;
import it.polimi.ingsw.messages.toClient.TimeoutExpiredMessage;
import it.polimi.ingsw.messages.toClient.WelcomeBackMessage;
import it.polimi.ingsw.messages.toClient.game.ChooseLeaderCardsRequest;
import it.polimi.ingsw.messages.toClient.lobby.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.lobby.SendPlayerNicknamesMessage;
import it.polimi.ingsw.messages.toClient.lobby.WaitingInTheLobbyMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements ClientInterface {
    private Optional<String> nickname;
    private Optional<GameMode> gameMode;
    private boolean validNickname = false;

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
    public Client(String IPAddress, int port, View view, Optional<GameMode> gameMode, Optional<String> nickname){
        this.gameMode = gameMode;
        this.nickname = nickname;
        this.validNickname = nickname.isPresent();
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
    public Client(String IPAddress, int port, View view) {
        this.nickname = Optional.empty();
        this.gameMode = Optional.empty();
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
        if (!packetReceiver.isAlive())
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
                    connected.set(false);
                    pinger.interrupt();
                    packetReceiver.interrupt();
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
            packetReceiver.interrupt();
        } finally {
            //closeSocket();
        }
    }

    @Override
    public void sendMessageToServer(Serializable message){
        if (connected.get() && !gameCanceled){
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
                closeSocket();
                return;
            }
            System.out.println(message.toString());
            if (message instanceof ChooseLeaderCardsRequest || message instanceof NumberOfPlayersRequest)
                gameCanceled = false;
            if (message instanceof WaitingInTheLobbyMessage || message instanceof SendPlayerNicknamesMessage || message instanceof NumberOfPlayersRequest || message instanceof WelcomeBackMessage)
                validNickname = true;
            //System.out.println(message.toString());
            ((MessageToClient) message).handleMessage(view);
        }
    }

    public void closeSocket() {
        if (connectionClosed)
            return;
        connectionClosed = true;
        boolean wasConnected = connected.getAndSet(false);
        if (!wasConnected)
            return;
        if (packetReceiver.isAlive())
            packetReceiver.interrupt();
        if (serverObserver.isAlive())
            serverObserver.interrupt();
        view.handleCloseConnection(wasConnected);
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



    public boolean isConnected() {
        return connected.get();
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public int getPort() {
        return port;
    }

    public void killThreads(){
        packetReceiver.interrupt();
        pinger.interrupt();
        serverObserver.interrupt();
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

    public void setNickname(String nickname){
        this.nickname = Optional.of(nickname);
    }

    public void setGameMode(GameMode gameMode){
        this.gameMode = Optional.of(gameMode);
    }

    public boolean isNicknameValid(){
        return validNickname;
    }

    public Optional<String> getNickname() {
        return nickname;
    }

    public Optional<GameMode> getGameMode() {
        return gameMode;
    }
}
