package it.polimi.ingsw.Server;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toClient.NicknameRequest;
import it.polimi.ingsw.messages.toClient.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.PlayersReadyToStartMessage;
import it.polimi.ingsw.messages.toClient.WaitingInTheLobbyMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Server implements ServerInterface {
    //Server's port
    private int port;
    //Thread pool which contains a thread for each client connected to the server
    private final ExecutorService executor;
    //Server.Server socket, used to accept connections from new client, it is constructed only when the server start working
    private ServerSocket serverSocket;

    private int numberOfPlayersForNextGame = -1;

    private List<ClientHandler> clientsInLobby;
    private Map<String, Controller> clientsDisconnected;

    private List<Controller> activeGames;

    ReentrantLock lockLobby = new ReentrantLock(true);
    ReentrantLock lockGames = new ReentrantLock(true);


    public static final Logger SERVER_LOGGER = Logger.getLogger("Server logger");


    public Server(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.clientsInLobby = new LinkedList<ClientHandler>();
        this.clientsDisconnected = new HashMap<>();
        this.activeGames = new LinkedList<>();
    }

    /**
     * Method used to start the server
     */
    public void startServer() {
        //First, I try to start the server, through its server socket. If the port is already in use an exception will be thrown
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            //I can write in the log that the server had a problem
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
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                executor.submit(clientHandler);
            }
        } catch (IOException e) {
            SERVER_LOGGER.log(Level.SEVERE, "An exception caused the server to stop working.");
        }
    }

    /**
     * Method used to handle the choice of a nickname.
     * If GameMode is Single player it makes the game starts
     * If the GameMode is Multiplayer:
     * - it adds the ClientHandler that has sent his nicknames to the list of waiting clients (if not already present)
     * - it calls the NewGameManager method
     * @param connection
     */

    public void handleNicknameChoice(ClientHandler connection) {
        //SOLO MODE -> start the game
        if (connection.getGameMode() == GameMode.SINGLE_PLAYER) {
            startNewGame(connection);
            return;
        }
        if(knownClient(connection.getNickname())){
            clientsDisconnected.get(connection.getNickname()).getPlayerByNickname(connection.getNickname()).setActive(true);
            clientsDisconnected.get(connection.getNickname()).addConnection(connection);
            clientsDisconnected.remove(connection.getNickname());
        }

        //MULTIPLAYER
        lockLobby.lock();
        try {
            if(!clientsInLobby.contains(connection)){
                clientsInLobby.add(connection);
            }
            NewGameManager();
            if (clientsInLobby.contains(connection) && connection.getClientHandlerPhase() == ClientHandlerPhase.WAITING_IN_THE_LOBBY)
                connection.sendMessageToClient(new WaitingInTheLobbyMessage());
        }
        finally {
            lockLobby.unlock();
        }

    }

    /**
     * Method used to check the state of the waiting clients. In particular:
     * - If the number of players has not been asked, it sends a request to the first player of the queue
     * - If the number of players is already been decided, it checks whether a game is ready to start. In particular it checks:
     *   a) If there are enough players in the lobby
     *   b) If the nicknames of the players who will join the game are unique
     * - If both a) and b) are true a new multiplayer game starts
     */
    @Override
    public void NewGameManager() {
        lockLobby.lock();
        try{
            if(numberOfPlayersForNextGame == -1 && clientsInLobby.get(0).getClientHandlerPhase() != ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS){
                clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS);
                clientsInLobby.get(0).sendMessageToClient(new NumberOfPlayersRequest(false));
            }else if(numberOfPlayersForNextGame != -1 && clientsInLobby.size() >= numberOfPlayersForNextGame){
                if(!duplicatesNicknameForNextMatch()){
                    startNewGame();
                }else{
                    askNicknameToFirstDuplicate();
                }
            }
        } finally {
            lockLobby.unlock();
        }
    }

    /**
     * Method that checks whether there are duplicates nicknames in the group of players who will join the next match
     * @return
     */
    private boolean duplicatesNicknameForNextMatch() {
        lockLobby.lock();
        try {
            for (int i = 1; i < numberOfPlayersForNextGame; i++) {
                for (int j = 0; j < i; j++) {
                    if (clientsInLobby.get(j).getNickname().equals(clientsInLobby.get(i).getNickname())) {
                        return true;
                    }
                }
            }
        } finally {
            lockLobby.unlock();
        }
        return false;
    }

    /**
     * Method used to manage the start of a multiplayer game
     */
    private void startNewGame() {
        Controller controller = null;
        try {
            controller = new Controller(GameMode.MULTI_PLAYER);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        lockLobby.lock();
        try {
            List <String> playersInGame = clientsInLobby.stream().filter(x -> clientsInLobby.indexOf(x) < numberOfPlayersForNextGame).map(x -> x.getNickname()).collect(Collectors.toList());
            for (int i = 0; i < numberOfPlayersForNextGame; i++) {
                clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
                clientsInLobby.get(0).sendMessageToClient(new PlayersReadyToStartMessage(playersInGame));
                clientsInLobby.get(0).setGameStarted(true);
                controller.addConnection(clientsInLobby.get(0));
                clientsInLobby.get(0).setController(controller);
                clientsInLobby.remove(0);
            }
            lockGames.lock();
            try {
                activeGames.add(controller);
            } finally {
                lockGames.unlock();
            }
            assert controller != null;
            controller.start();
            numberOfPlayersForNextGame = -1;
            if (clientsInLobby.size() > 0) {
                clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS);
                clientsInLobby.get(0).sendMessageToClient(new NumberOfPlayersRequest(false));
            }
        } finally {
            lockLobby.unlock();
        }
    }

    private void startNewGame(ClientHandler connection){
        Controller controller = null;
        try {
            controller = new Controller(GameMode.SINGLE_PLAYER);
        } catch (InvalidArgumentException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connection.setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
        connection.setGameStarted(true);
        controller.addConnection(connection);
        connection.setController(controller);
        lockGames.lock();
        try{
            activeGames.add(controller);
        } finally{
            lockGames.unlock();
        }
        assert controller != null;
        controller.start();

    }

    /**
     * Method used to ask the nickname to the first duplicate of the queue
     */
    private void askNicknameToFirstDuplicate() {
        boolean found = false;
        lockLobby.lock();
        try {
            for(int i = 1; i < numberOfPlayersForNextGame; i++) {
                for (int j = 0; j < i; j++) {
                    if (!found && clientsInLobby.get(j).getNickname().equals(clientsInLobby.get(i).getNickname()) && clientsInLobby.get(i).getClientHandlerPhase() != ClientHandlerPhase.WAITING_NICKNAME) {
                        clientsInLobby.get(i).setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
                        clientsInLobby.get(i).sendMessageToClient(new NicknameRequest(true, true));
                        found = true;
                    }
                }
            }
        } finally {
            lockLobby.unlock();
        }
    }


    //TODO still testing but should work
    public void removeConnection(ClientHandler connection){
        int position = -1;
        try{
            lockLobby.lock();
            position = clientsInLobby.indexOf(connection);
            if (position > -1) {
                clientsInLobby.remove(connection);
                if (position == 0)
                    numberOfPlayersForNextGame = -1;
                if(position < numberOfPlayersForNextGame)
                    NewGameManager();
            }
        }
        finally {
            lockLobby.unlock();
        }
    }


    @Override
    public void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame){
        this.numberOfPlayersForNextGame = numberOfPlayersForNextGame;
        Server.SERVER_LOGGER.log(Level.INFO, "New message from "+ clientHandler.getNickname() + " that has chosen the number of players: "+ numberOfPlayersForNextGame);
        NewGameManager();
    }

    public void handleDisconnection(ClientHandler clientHandler){
        if (clientHandler.isGameStarted())
            clientsDisconnected.put(clientHandler.getNickname(), clientHandler.getController());
        else
            removeConnection(clientHandler);
    }

    public void handleReconnection(ClientHandler clientHandler){

    }

    private boolean knownClient(String nickname) {
        return clientsDisconnected.containsKey(nickname);
    }

}