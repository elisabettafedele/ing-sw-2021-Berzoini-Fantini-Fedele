package it.polimi.ingsw.server;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.game_phases.PlayPhase;
import it.polimi.ingsw.controller.game_phases.SetUpPhase;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import it.polimi.ingsw.messages.toClient.WelcomeBackMessage;
import it.polimi.ingsw.messages.toClient.game.GameOverMessage;
import it.polimi.ingsw.messages.toClient.lobby.NicknameRequest;
import it.polimi.ingsw.messages.toClient.lobby.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.lobby.SendPlayerNicknamesMessage;
import it.polimi.ingsw.messages.toClient.lobby.WaitingInTheLobbyMessage;
import it.polimi.ingsw.messages.toClient.matchData.LoadDevelopmentCardsMessage;
import it.polimi.ingsw.messages.toClient.matchData.LoadLeaderCardsMessage;
import it.polimi.ingsw.jsonParsers.LightCardsParser;
import it.polimi.ingsw.model.persistency.GameHistory;
import it.polimi.ingsw.model.player.Player;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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
    private Map<String, GameOverMessage> clientsDisconnectedGameFinished;
    private List<String> takenNicknames;


    private List<Controller> activeGames;

    ReentrantLock lockLobby = new ReentrantLock(true);
    ReentrantLock lockGames = new ReentrantLock(true);


    public static final Logger SERVER_LOGGER = Logger.getLogger("Server logger");


    public Server(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.clientsInLobby = new LinkedList<>();
        this.clientsDisconnected = new HashMap<>();
        this.clientsDisconnectedGameFinished = new HashMap<>();
        this.activeGames = new LinkedList<>();
        this.takenNicknames = new ArrayList<>();
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
     * @param connection the connection with the client that has chosen his nickname
     */

    public synchronized void handleNicknameChoice(ClientHandler connection) {
        if(knownClient(connection.getNickname())){
            if (!handleKnownClientReconnection(connection))
                return;
        }

        if (takenNicknames.contains(connection.getNickname())){
            connection.setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
            connection.sendMessageToClient(new NicknameRequest(true, true));
            return;
        } else{
            takenNicknames.add(connection.getNickname());
            connection.setValidNickname(true);
        }

        //SOLO MODE -> start the game
        if (connection.getGameMode() == GameMode.SINGLE_PLAYER) {
            startNewGame(connection);
            return;
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

    public boolean handleKnownClientReconnection(ClientHandler clientHandler){
        boolean gameFinished = clientsDisconnectedGameFinished.containsKey(clientHandler.getNickname());
        clientHandler.sendMessageToClient(new WelcomeBackMessage(clientHandler.getNickname(), gameFinished));

        if (gameFinished){
            clientHandler.sendMessageToClient(clientsDisconnectedGameFinished.get(clientHandler.getNickname()));
            clientsDisconnectedGameFinished.remove(clientHandler.getNickname());
            return true;
        } else {
            clientHandler.setGameStarted(true);
            clientsDisconnected.get(clientHandler.getNickname()).addConnection(clientHandler);
            clientHandler.setController(clientsDisconnected.get(clientHandler.getNickname()));
            clientsDisconnected.get(clientHandler.getNickname()).getPlayerByNickname(clientHandler.getNickname()).setActive(true);
            clientHandler.sendMessageToClient(new SendPlayerNicknamesMessage(clientHandler.getNickname(), clientsDisconnected.get(clientHandler.getNickname()).getNicknames().stream().filter(x -> !x.equals(clientHandler.getNickname())).collect(Collectors.toList())));
            clientHandler.sendMessageToClient(new LoadDevelopmentCardsMessage(LightCardsParser.getLightDevelopmentCards(DevelopmentCardParser.parseCards())));
            clientHandler.sendMessageToClient(new LoadLeaderCardsMessage(LightCardsParser.getLightLeaderCards(LeaderCardParser.parseCards())));
            if (clientsDisconnected.get(clientHandler.getNickname()).getGamePhase() instanceof SetUpPhase){
                clientHandler.setClientHandlerPhase(ClientHandlerPhase.SET_UP_FINISHED);
                ((SetUpPhase) clientsDisconnected.get(clientHandler.getNickname()).getGamePhase()).endPhaseManager(clientHandler);
            } else {
                clientHandler.getController().sendMatchData(clientHandler.getController().getGame(), clientHandler, false);
                clientsDisconnected.remove(clientHandler.getNickname());
            }
            return false;
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
    public synchronized void NewGameManager() {
        lockLobby.lock();
        try{
            if(numberOfPlayersForNextGame == -1 && clientsInLobby.get(0).getClientHandlerPhase() != ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS){
                clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS);
                clientsInLobby.get(0).sendMessageToClient(new NumberOfPlayersRequest());
            }else if(numberOfPlayersForNextGame != -1 && clientsInLobby.size() >= numberOfPlayersForNextGame){
                if(!invalidNicknameForNextMatch())
                    startNewGame();
            }
        } finally {
            lockLobby.unlock();
        }
    }

    private boolean invalidNicknameForNextMatch(){
        lockLobby.lock();
        try {
            for (int i = 1; i < numberOfPlayersForNextGame; i++) {
                if (!clientsInLobby.get(i).isValidNickname())
                    return true;
            }
        } finally {
            lockLobby.unlock();
        }
        return false;
    }

    /**
     * Method that checks whether there are duplicates nicknames in the group of players who will join the next match
     * @return true iff there are duplicates nicknames among the players that are ready to start a new game
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
        if (clientsInLobby.size() < numberOfPlayersForNextGame)
            return;
        Controller controller = null;
        try {
            controller = new Controller(GameMode.MULTI_PLAYER);
            controller.setServer(this);
        } catch (InvalidArgumentException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        lockLobby.lock();
        try {
            List <String> playersInGame = clientsInLobby.stream().filter(x -> clientsInLobby.indexOf(x) < numberOfPlayersForNextGame).map(x -> x.getNickname()).collect(Collectors.toList());
            for (int i = 0; i < numberOfPlayersForNextGame; i++) {
                clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
                clientsInLobby.get(0).sendMessageToClient(new SendPlayerNicknamesMessage(clientsInLobby.get(0).getNickname(), playersInGame.stream().filter(x -> !x.equals(clientsInLobby.get(0).getNickname())).collect(Collectors.toList())));
                clientsInLobby.get(0).setGameStarted(true);
                controller.addConnection(clientsInLobby.get(0));
                clientsInLobby.get(0).setController(controller);
                clientsInLobby.remove(0);
            }
            controller.setControllerID(playersInGame.stream().sorted().reduce("", String::concat).hashCode());
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
                clientsInLobby.get(0).sendMessageToClient(new NumberOfPlayersRequest());
            }
        } finally {
            lockLobby.unlock();
        }
    }

    private void startNewGame(ClientHandler connection){
        Controller controller = null;
        try {
            controller = new Controller(GameMode.SINGLE_PLAYER);
            controller.setServer(this);
        } catch (InvalidArgumentException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connection.setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
        connection.setGameStarted(true);
        controller.addConnection(connection);
        controller.setControllerID(connection.getNickname().hashCode());
        connection.setController(controller);
        lockGames.lock();
        try{
            activeGames.add(controller);
        } finally{
            lockGames.unlock();
        }
        assert controller != null;
        connection.sendMessageToClient(new SendPlayerNicknamesMessage(connection.getNickname(), new ArrayList<String>()));
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


    /**
     * Method to handle disconnection of clients that has not been added to a game
     * @param connection
     */
    public void removeConnectionLobby(ClientHandler connection){
        int position = -1;
        try{
            lockLobby.lock();
            //If the client has already taken a valid nickname, I remove it from the list.
            if (connection.getClientHandlerPhase() != ClientHandlerPhase.WAITING_NICKNAME && connection.getClientHandlerPhase() !=ClientHandlerPhase.WAITING_GAME_MODE)
                takenNicknames.remove(connection.getNickname());
            position = clientsInLobby.indexOf(connection);
            if (position > -1) {
                clientsInLobby.remove(connection);
                takenNicknames.remove(connection.getNickname());
                if (position == 0)
                    numberOfPlayersForNextGame = -1;
                if(position < numberOfPlayersForNextGame || numberOfPlayersForNextGame == -1)
                    NewGameManager();
            }
        }
        finally {
            lockLobby.unlock();
        }
    }

    /**
     * Method to remove a connection when the game is already started.
     * If it is the last connection, also the controller is deleted from the list of active games
     * @param connection the {@link ClientHandler} to be removed from the controller
     * @return true iff the game still exist
     */
    public synchronized boolean removeConnectionGame(ClientHandler connection, boolean forced){
        //If he was the last player remained in the game, I delete the game and I remove all the players from disconnectedPlayers -> the game is not finished, but is not playable anymore
        if (connection.getController().getClientHandlers().size() == 1) {
            for (String nickname : connection.getController().getPlayers().stream().map(Player::getNickname).collect(Collectors.toList())) {
                clientsDisconnected.remove(nickname);
                takenNicknames.remove(nickname);
            }
            GameHistory.removeOldGame(connection.getController().getControllerID());
            activeGames.remove(connection.getController());
            return false;
        } else {
            if (!forced)
                clientsDisconnected.put(connection.getNickname(), connection.getController());
            connection.getController().removeConnection(connection);
            return true;
        }
    }


    @Override
    public void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame){
        this.numberOfPlayersForNextGame = numberOfPlayersForNextGame;
        Server.SERVER_LOGGER.log(Level.INFO, "New message from "+ clientHandler.getNickname() + " that has chosen the number of players: "+ numberOfPlayersForNextGame);
        NewGameManager();
    }

    /**
     * Method to handle the end of a game. In particular:
     * - If the game had some clients disconnected: they are moved from clientDisconnected to clientsDisconnectedGameFinished and the results of the game are saved in the GameOver message
     * - Anyhow, the game is removed from activeGames, since it is no longer active
     * @param controller
     * @param gameOverMessage
     */
    public void gameEnded(Controller controller, GameOverMessage gameOverMessage){
        controller.getPlayers().forEach(x -> takenNicknames.remove(x.getNickname()));
        List<String> disconnectedClientsNicknames = controller.getPlayers().stream().filter(x -> !x.isActive()).map(x -> x.getNickname()).collect(Collectors.toList());
        for (String nickname : disconnectedClientsNicknames) {
            clientsDisconnected.remove(nickname);
            clientsDisconnectedGameFinished.put(nickname, gameOverMessage);
        }
        GameHistory.removeOldGame(controller.getControllerID());
        activeGames.remove(controller);
    }

    public void gameEnded(Controller controller){
        takenNicknames.remove(controller.getPlayers().get(0).getNickname());
        GameHistory.removeOldGame(controller.getControllerID());
        activeGames.remove(controller);
    }

    private boolean knownClient(String nickname) {
        return clientsDisconnected.containsKey(nickname) || clientsDisconnectedGameFinished.containsKey(nickname);
    }

}