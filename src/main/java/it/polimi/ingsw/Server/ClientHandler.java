package it.polimi.ingsw.Server;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.toClient.GameModeRequest;
import it.polimi.ingsw.messages.toClient.TimeoutExpiredMessage;
import it.polimi.ingsw.messages.toServer.MessageToServer;


import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

public class ClientHandler implements Runnable, ClientHandlerInterface {
    public static final int PING_PERIOD = 5000; //PING_PERIOD = TIMEOUT/2
    //The timer gives one minute to the user to send the response
    public static final int SO_TIMEOUT_PERIOD = 100000;
    public static final int TIMEOUT_FOR_RESPONSE = 15000;

    private final Socket socket;

    private final String IPAddress;
    private final int port;
    //For the game I will use an object stream, but to the debug the server it is easier to use just a simple reader and writer
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Server server;

    private Action currentAction;

    private Thread timer;

    private final Thread pinger;
    private boolean active = false;

    public Controller getController() {
        return controller;
    }

    private Controller controller;

    public boolean isGameStarted() {
        return gameStarted;
    }

    private boolean gameStarted = false;

    private String nickname;
    private GameMode gameMode;

    private ClientHandlerPhase clientHandlerPhase;

    /**
     * Class constructor. It enables a ping message that check the client's connection until it become inactive
     * NB the client becomes inactive when the internalSend throws an IOException
     * @param socket
     */
    public ClientHandler(Socket socket, Server server){
        this.socket = socket;
        this.IPAddress = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.server = server;
        this.pinger = new Thread(() -> {
            while (active){
                try{
                    Thread.sleep(PING_PERIOD);
                    sendMessageToClient(ConnectionMessage.PING);
                }catch (InterruptedException e){
                    break;
                }
            }
        });
    }

    public void run(){
        try {
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            active = true;
            socket.setSoTimeout(SO_TIMEOUT_PERIOD);
            pinger.start();

            clientHandlerPhase = ClientHandlerPhase.WAITING_GAME_MODE;
            sendMessageToClient(new GameModeRequest());
            //startTimer();


            while(active){
                try {
                    Object messageFromClient = is.readObject();
                    if(messageFromClient != null && !(messageFromClient == ConnectionMessage.PING)) {
                        //stopTimer();
                        ((MessageToServer) messageFromClient).handleMessage(server, this);
                    }
                } catch (ClassNotFoundException messageIgnored) {
                } catch (SocketTimeoutException e){ //when the timer has expired
                    sendMessageToClient(new TimeoutExpiredMessage());
                    handleSocketDisconnection();
                } catch (IOException e){//when the client is no longer connected
                    handleSocketDisconnection();
                }

            }
        }catch (IOException e){
            handleSocketDisconnection();
        }
    }

    @Override
    public void startTimer(){
        timer = new Thread(() -> {
            try{
                Thread.sleep(TIMEOUT_FOR_RESPONSE);
                Server.SERVER_LOGGER.log(Level.SEVERE, "Timer has expired, you have been disconnected.");
                handleSocketDisconnection();
            } catch (InterruptedException e){ }
        });
        timer.start();
    }

    public void stopTimer(){
        if (timer != null && timer.isAlive()){
            timer.interrupt();
            timer = null;
        }
    }

    @Override
    public void sendMessageToClient(Serializable message) {
        try {
            os.writeObject(message);
            os.flush();
            os.reset();
            //startTimer();
        } catch (IOException e) {
            handleSocketDisconnection();
        }
    }

    /**
     * Method to handle client's disconnection
     */
    //If the timer is expired or the ping message cannot be sent due to disconnection of the client (it throws IO Exception) I tell the client that he has been disconnected
    private void handleSocketDisconnection(){
        //The connection is not active anymore
        this.active = false;
        Server.SERVER_LOGGER.log(Level.SEVERE, "Client disconnected");
        //If the game is started, I mark the player as disconnected and the turn controller will not handle its turn
        if (gameStarted){
            //TODO
        } else {
            //If the game is not started yet, I simply remove the player from the list of waiting players
            try {
                os.writeObject(ConnectionMessage.CONNECTION_CLOSED);
                os.flush();
                os.reset();
            } catch (IOException e) { }
            server.removeConnectionBetti(this);
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
    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public String getNickname(){
        return nickname;
    }

    @Override
    public ClientHandlerPhase getClientHandlerPhase(){
        return clientHandlerPhase;
    }

    @Override
    public Action getCurrentAction() {
        return currentAction;
    }

    @Override
    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    @Override
    public void setNickname(String nickname){
        this.nickname = nickname;
        server.handleNicknameChoice(this);
    }

    @Override
    public void setClientHandlerPhase(ClientHandlerPhase clientHandlerPhase){
        this.clientHandlerPhase = clientHandlerPhase;
    }

    @Override
    public void setGameMode(GameMode gameMode){
        this.gameMode = gameMode;
    }

    @Override
    public void setGameStarted(boolean gameStarted){
        this.gameStarted = gameStarted;
    }
}
