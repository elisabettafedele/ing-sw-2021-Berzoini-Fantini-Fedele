package it.polimi.ingsw.Server;

import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.toClient.GameModeRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;


import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

public class ClientHandler implements Runnable{
    public static final int PING_PERIOD = 8000; //PING_PERIOD = TIMEOUT/2
    //The timer gives one minute to the user to send the response
    public static final int SO_TIMEOUT_PERIOD = 160000;
    public static final int TIMEOUT_FOR_RESPONSE = 60000;


    private final Socket socket;
    //For the game I will use an object stream, but to the debug the server it is easier to use just a simple reader and writer
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Server server;

    private Thread timer;

    private final Thread pinger;
    private boolean active = false;
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

            while(active){
                try {
                    Object messageFromClient = is.readObject();
                    if(messageFromClient != null && !(messageFromClient == ConnectionMessage.PING)) {
                        ((MessageToServer) messageFromClient).handleMessage(server, this);
                    }
                } catch (ClassNotFoundException e) {
                    handleSocketDisconnection();
                } catch (SocketTimeoutException e){ //when the client is no longer connected
                    handleSocketDisconnection();
                }

            }
        }catch (IOException e){
            handleSocketDisconnection();
        }
    }

    /**
     * Timer used to disconnect players who are too slow in sending their responses
     */
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

    /**
     * Method used to send message to the client, through an object stream
     * @param message the message to be sent
     */
    public void sendMessageToClient(Serializable message) {
        try {
            os.writeObject(message);
            os.flush();
            os.reset();
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
            server.removeConnection(this);
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

    public GameMode getGameMode() {
        return gameMode;
    }

    public String getNickname(){
        return nickname;
    }

    public ClientHandlerPhase getClientHandlerPhase(){
        return clientHandlerPhase;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setClientHandlerPhase(ClientHandlerPhase clientHandlerPhase){
        this.clientHandlerPhase = clientHandlerPhase;
    }

    public void setGameMode(GameMode gameMode){
        this.gameMode = gameMode;
    }

    public void setGameStarted(boolean gameStarted){
        this.gameStarted = gameStarted;
    }
}
