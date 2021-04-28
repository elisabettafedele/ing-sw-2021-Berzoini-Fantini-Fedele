package it.polimi.ingsw.Server;

import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.messages.toClient.NicknameRequest;
import it.polimi.ingsw.messages.toClient.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.PlayersReadyToStartMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    //Server's port
    private int port;
    //Thread pool which contains a thread for each client connected to the server
    private final ExecutorService executor;
    //Server.Server socket, used to accept connections from new client, it is constructed only when the server start working
    private ServerSocket serverSocket;

    private int numberOfPlayersForNextGame = -1;

    private List<ClientHandler> clientsInLobby;

    ReentrantLock lockLobby = new ReentrantLock(true);


    public static final Logger SERVER_LOGGER = Logger.getLogger("Server logger");


    public Server(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.clientsInLobby = new LinkedList<ClientHandler>();
    }

    public void startServer() {
        //First, I try to start the server, through its server socket. If the port is already in use an exception will be thrown
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            //I can write in the log that the server had a problem but I still do not know how the log works, so I will write it in system out
            SERVER_LOGGER.log(Level.SEVERE, "Cannot open server on port " + port);
            return;
        }

        //Ok from now on the server is actually working
        SERVER_LOGGER.log(Level.INFO, "Server ready");

        try {
            //Until the server is stopped, he keeps accepting new connections from clients who connect to its socket
            while (true) {
                Socket clientSocket = serverSocket.accept();
                SERVER_LOGGER.log(Level.INFO,"New client connection: [IP address: " + clientSocket.getInetAddress().getHostAddress() + ", port: " + port + "]");
                ClientHandler clientConnection = new ClientHandler(clientSocket, this);

                executor.submit(clientConnection);
            }
        } catch (IOException e) {
            SERVER_LOGGER.log(Level.SEVERE, "An exception caused the server to stop working.");
        }
    }

    public void handleNicknameChoice(ClientHandler connection) {
        //SOLO MODE -> start the game
        if (connection.getGameMode() == GameMode.SINGLE_PLAYER && connection.getClientHandlerPhase() == ClientHandlerPhase.WAITING_NICKNAME) {
            //TODO handleNewSoloGame
            return;
        }

        //MULTIPLAYER
        try {
            lockLobby.lock();

            if(connection.getClientHandlerPhase() == ClientHandlerPhase.WAITING_NICKNAME){
                if(!clientsInLobby.contains(connection)){
                    clientsInLobby.add(connection);
                }
                connection.setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
                NewGameManager();
            }

        }
        finally {
            lockLobby.unlock();
        }

    }

    public void NewGameManager() {
        if(numberOfPlayersForNextGame == -1 && clientsInLobby.get(0).getClientHandlerPhase() != ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS){
            clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS);
            clientsInLobby.get(0).sendMessageToClient(new NumberOfPlayersRequest(false));
            return;
        }else if(numberOfPlayersForNextGame != -1 && clientsInLobby.size() >= numberOfPlayersForNextGame){
            if(!duplicatesNicknameForNextMatch()){
                startNewGame();
            }else{
                askNicknameToFirstDuplicate();
            }
        }
    }


    private boolean duplicatesNicknameForNextMatch() {

        for(int i = 1; i < numberOfPlayersForNextGame; i++){
            for(int j = 0; j < i; j++){
                if (clientsInLobby.get(j).getNickname().equals(clientsInLobby.get(i).getNickname())){
                    return true;
                }
            }
        }
        return false;
    }

    private void startNewGame() {
        for(int i = 0; i < numberOfPlayersForNextGame; i++){
            clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
            clientsInLobby.get(0).sendMessageToClient(new PlayersReadyToStartMessage());
            clientsInLobby.remove(0);
        }
        numberOfPlayersForNextGame = -1;
        if (clientsInLobby.size() > 0){
            clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS);
            clientsInLobby.get(0).sendMessageToClient(new NumberOfPlayersRequest(false));
        }
    }

    private void askNicknameToFirstDuplicate() {
        for(int i = 1; i < numberOfPlayersForNextGame; i++) {
            for (int j = 0; j < i; j++) {
                if (clientsInLobby.get(j).getNickname().equals(clientsInLobby.get(i).getNickname())) {
                    clientsInLobby.get(i).setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
                    clientsInLobby.get(i).sendMessageToClient(new NicknameRequest(true, true));
                }
            }
        }
    }
    //Just a try
    public void removeConnection2(ClientHandler connection){
        try{
            lockLobby.lock();
            if (clientsInLobby.contains(connection)){
                if(clientsInLobby.get(0).equals(connection)){
                    numberOfPlayersForNextGame = -1;
                }
                clientsInLobby.remove(connection);
                NewGameManager();
            }
        }finally {
            lockLobby.unlock();
        }
    }

    public void removeConnection(ClientHandler connection){
        try{
            lockLobby.lock();
            if (clientsInLobby.contains(connection))
                clientsInLobby.remove(connection);
        }
        finally {
            lockLobby.unlock();
        }
    }

    public void setNumberOfPlayersForNextGame(int numberOfPlayersForNextGame){
        this.numberOfPlayersForNextGame = numberOfPlayersForNextGame;
    }

}