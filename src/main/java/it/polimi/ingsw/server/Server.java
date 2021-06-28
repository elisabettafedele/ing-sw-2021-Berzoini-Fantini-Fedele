package it.polimi.ingsw.server;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.game_phases.EndPhase;
import it.polimi.ingsw.controller.game_phases.MultiplayerEndPhase;
import it.polimi.ingsw.controller.game_phases.PlayPhase;
import it.polimi.ingsw.controller.game_phases.SetUpPhase;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import it.polimi.ingsw.messages.toClient.matchData.TurnMessage;
import it.polimi.ingsw.messages.toClient.WelcomeBackMessage;
import it.polimi.ingsw.messages.toClient.game.GameOverMessage;
import it.polimi.ingsw.messages.toClient.lobby.NicknameRequest;
import it.polimi.ingsw.messages.toClient.lobby.NumberOfPlayersRequest;
import it.polimi.ingsw.messages.toClient.lobby.SendPlayersNicknamesMessage;
import it.polimi.ingsw.messages.toClient.lobby.WaitingInTheLobbyMessage;
import it.polimi.ingsw.messages.toClient.matchData.LoadDevelopmentCardsMessage;
import it.polimi.ingsw.messages.toClient.matchData.LoadLeaderCardsMessage;
import it.polimi.ingsw.jsonParsers.LightCardsParser;
import it.polimi.ingsw.jsonParsers.GameHistory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * Class used to manage new connecting clients, the start of new matches and the end of other matches
 */
public class Server implements ServerInterface {
    //Server's port
    private int port;
    //Thread pool which contains a thread for each client connected to the server
    private final ExecutorService executor;
    //Server socket, used to accept connections from new client, it is constructed only when the server start working
    private ServerSocket serverSocket;
    private int numberOfPlayersForNextGame = -1;
    private List<ClientHandler> clientsInLobby;
    private Map<String, Controller> clientsDisconnected;
    private Map<String, GameOverMessage> clientsDisconnectedGameFinished;
    private Set<String> takenNicknames;
    private List<Controller> activeGames;
    private ReentrantLock lockLobby = new ReentrantLock(true);
    private ReentrantLock lockGames = new ReentrantLock(true);
    public static final Logger SERVER_LOGGER = Logger.getLogger("Server logger");
    private boolean saveLog;


    public Server(int port, boolean saveLog) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
        this.clientsInLobby = new LinkedList<>();
        this.clientsDisconnected = new HashMap<>();
        this.clientsDisconnectedGameFinished = new HashMap<>();
        this.activeGames = new LinkedList<>();
        this.takenNicknames = new HashSet<>();
        this.saveLog = saveLog;
    }

    /**
     * Method used to start the server
     */
    public void startServer() {
        //First, I try to start the server, through its server socket. If the port is already in use an exception will be thrown
        if (saveLog)
            initLogger();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            //I can write in the log that the server had a problem
            SERVER_LOGGER.log(Level.SEVERE, "Cannot open server on port " + port);
            return;
        }

        //Ok from now on the server is actually working
        SERVER_LOGGER.log(Level.INFO, "Server ready on port " + port);

        try {
            //Until the server is stopped, he keeps accepting new connections from clients who connect to its socket
            while (true) {
                Socket clientSocket = serverSocket.accept();
                SERVER_LOGGER.log(Level.INFO,"Received connection from address: [" + clientSocket.getInetAddress().getHostAddress() + "]");
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                executor.submit(clientHandler);
            }
        } catch (IOException e) {
            SERVER_LOGGER.log(Level.SEVERE, "An exception caused the server to stop working.");
        }
    }

    private void initLogger() {
        Date date = GregorianCalendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM_HH.mm.ss");
        try {
            FileHandler fh = new FileHandler("server-" + dateFormat.format(date) + ".log");
            fh.setFormatter(new SimpleFormatter());
            SERVER_LOGGER.addHandler(fh);
        } catch (IOException e) {
            SERVER_LOGGER.severe(e.getMessage());
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
            // If the game the player was playing in is not finished, I do not need to start a new game.
            // The method handleKnownClientReconnection() will manage the reinsertion of the player in the right room.
            if (!handleKnownClientReconnection(connection))
                return;
        }

        if (takenNicknames.contains(connection.getNickname())){
            //If there is an old single player game to finish
            if (connection.getGameMode() == GameMode.SINGLE_PLAYER && GameHistory.retrieveGameFromControllerId(connection.getNickname().hashCode()) != null){
                startNewGame(connection);
                return;
            }
            connection.setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
            connection.sendMessageToClient(new NicknameRequest(true, true));
            return;
        } else{
            takenNicknames.add(connection.getNickname());
            connection.setValidNickname(true);
        }

        //SOLO MODE -> start the game
        if (connection.getGameMode() == GameMode.SINGLE_PLAYER) {
            takenNicknames.add(connection.getNickname());
            startNewGame(connection);
            return;
        }

        //MULTIPLAYER
        lockLobby.lock();
        try {
            if(!clientsInLobby.contains(connection)){
                clientsInLobby.add(connection);
            }
            newGameManager();
            if (clientsInLobby.contains(connection) && connection.getClientHandlerPhase() == ClientHandlerPhase.WAITING_IN_THE_LOBBY)
                connection.sendMessageToClient(new WaitingInTheLobbyMessage());
        }
        finally {
            lockLobby.unlock();
        }

    }

    /**
     * Method to handle the reconnection of a client that was playing a multiplayer game or that has finished a single player game, but has not received the results.
     * @param clientHandler the {@link ClientHandler} of the reconnected player
     * @return true uf the game the player was playing in is now finished
     */
    public boolean handleKnownClientReconnection(ClientHandler clientHandler){
        boolean gameFinished = clientsDisconnectedGameFinished.containsKey(clientHandler.getNickname());
        if (!gameFinished && clientHandler.getGameMode() == GameMode.SINGLE_PLAYER){
            clientsDisconnected.remove(clientHandler.getNickname());
            takenNicknames.remove(clientHandler.getNickname());
            return true;
        }

        clientHandler.sendMessageToClient(new WelcomeBackMessage(clientHandler.getNickname(), gameFinished));

        if (gameFinished){
            //If the game is already finished, I send the results to the player and I manage his connection as a new one
            clientHandler.sendMessageToClient(clientsDisconnectedGameFinished.get(clientHandler.getNickname()));
            clientsDisconnectedGameFinished.remove(clientHandler.getNickname());
            return true;
        } else {
            clientHandler.setGameStarted(true);
            clientsDisconnected.get(clientHandler.getNickname()).addConnection(clientHandler);
            clientHandler.setController(clientsDisconnected.get(clientHandler.getNickname()));
            clientsDisconnected.get(clientHandler.getNickname()).getPlayerByNickname(clientHandler.getNickname()).setActive(true);
            clientHandler.sendMessageToClient(new SendPlayersNicknamesMessage(clientHandler.getNickname(), clientsDisconnected.get(clientHandler.getNickname()).getNicknames().stream().filter(x -> !x.equals(clientHandler.getNickname())).collect(Collectors.toList())));
            clientHandler.sendMessageToClient(new LoadDevelopmentCardsMessage(LightCardsParser.getLightDevelopmentCards(DevelopmentCardParser.parseCards())));
            clientHandler.sendMessageToClient(new LoadLeaderCardsMessage(LightCardsParser.getLightLeaderCards(LeaderCardParser.parseCards())));
            if (clientsDisconnected.get(clientHandler.getNickname()).getGamePhase() instanceof SetUpPhase){
                clientHandler.setClientHandlerPhase(ClientHandlerPhase.SET_UP_FINISHED);
                ((SetUpPhase) clientsDisconnected.get(clientHandler.getNickname()).getGamePhase()).endPhaseManager(clientHandler);
            } else {
                clientHandler.getController().sendMatchData(clientHandler.getController().getGame(), clientHandler, false);
                clientHandler.sendMessageToClient(new TurnMessage(((PlayPhase)clientHandler.getController().getGamePhase()).getTurnController().getCurrentPlayer().getNickname(), true));
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
    public synchronized void newGameManager() {
        lockLobby.lock();
        try{
            if(numberOfPlayersForNextGame == -1 && clientsInLobby.size() > 0 && clientsInLobby.get(0).getClientHandlerPhase() != ClientHandlerPhase.WAITING_NUMBER_OF_PLAYERS){
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

    /**
     * Method to check whether there are duplicates among the nicknames for the next match.
     * Nicknames need to be unique in the whole system
     * @return true if all the nicknames are valid
     */
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
     * Method used to manage the start of a multiplayer game
     */
    private void startNewGame() {
        //if (clientsInLobby.size() < numberOfPlayersForNextGame || !invalidNicknameForNextMatch()) maybe
        if (clientsInLobby.size() < numberOfPlayersForNextGame)
            return;
        Controller controller = new Controller(GameMode.MULTI_PLAYER);
        controller.setServer(this);
        lockLobby.lock();
        try {
            List <String> playersInGame = clientsInLobby.stream().filter(x -> clientsInLobby.indexOf(x) < numberOfPlayersForNextGame).map(x -> x.getNickname()).collect(Collectors.toList());

            for (int i = 0; i < numberOfPlayersForNextGame; i++) {
                clientsInLobby.get(0).setClientHandlerPhase(ClientHandlerPhase.READY_TO_START);
                clientsInLobby.get(0).setGameStarted(true);
                controller.addConnection(clientsInLobby.get(0));
                clientsInLobby.get(0).setController(controller);
                clientsInLobby.remove(0);
            }
            for (String nickname : playersInGame) {
                controller.getConnectionByNickname(nickname).sendMessageToClient(new SendPlayersNicknamesMessage(nickname, playersInGame.stream().filter(x -> !(x.equals(nickname))).collect(Collectors.toList())));
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

    /**
     * Method to handle the start of a single player {@link it.polimi.ingsw.model.game.Game}
     * @param connection the {@link ClientHandler} of the player
     */
    private void startNewGame(ClientHandler connection){
        Controller controller = new Controller(GameMode.SINGLE_PLAYER);
        controller.setServer(this);
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
        connection.sendMessageToClient(new SendPlayersNicknamesMessage(connection.getNickname(), new ArrayList<String>()));
        controller.start();
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
                    newGameManager();
            }
        }
        finally {
            lockLobby.unlock();
        }
    }

    /**
     * Method to remove a connection when the game is already started.
     * If the game was in the end phase I save the end message to send to the player
     * @param connection the {@link ClientHandler} to be removed from the controller
     */
    public synchronized void removeConnectionGame(ClientHandler connection){
        if (connection.getController().getGamePhase() instanceof EndPhase)
            clientsDisconnectedGameFinished.put(connection.getNickname(), ((MultiplayerEndPhase)connection.getController().getGamePhase()).getEndMessage(true));
        else
            clientsDisconnected.put(connection.getNickname(), connection.getController());
        connection.getController().removeConnection(connection);
    }

    /**
     * Method to handle the disconnection of a single player.
     * If he was still playing or he was in the set up phase the game remain saved in the json file, as it happens with persistency
     * @param connection the {@link ClientHandler} of the disconnected client
     */
    public synchronized void removeConnectionGameSinglePlayer(ClientHandler connection){
        //if the client comes back I retrieve the game from the json file
        if (!(connection.getController().getGamePhase() instanceof EndPhase)){
            takenNicknames.remove(connection.getNickname());
            activeGames.remove(connection.getController());
        }
    }

    /**
     * Method to remove a nickname from the list of takenNicknames
     * @param nickname
     */
    public void removeNickname (String nickname){
        clientsDisconnected.remove(nickname);
        takenNicknames.remove(nickname);
    }

    @Override
    public void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame){
        this.numberOfPlayersForNextGame = numberOfPlayersForNextGame;
        newGameManager();
    }

    /**
     * Method to handle the end of a multiplayer game. In particular:
     * - If the game had some clients disconnected: they are moved from clientDisconnected to clientsDisconnectedGameFinished and the results of the game are saved in the GameOver message
     * - Anyhow, the game is removed from activeGames, since it is no longer active
     * @param controller the {@link Controller} related to the game that has just ended
     * @param gameOverMessage the message with the results of the {@link it.polimi.ingsw.model.game.Game}
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

    /**
     * Method to handle the end of a single player game
     * @param controller the controller of the {@link it.polimi.ingsw.model.game.Game}
     */
    public void gameEnded(Controller controller){
        takenNicknames.remove(controller.getPlayers().get(0).getNickname());
        GameHistory.removeOldGame(controller.getControllerID());
        activeGames.remove(controller);
    }

    /**
     * @param nickname the nickname of the {@link it.polimi.ingsw.client.Client}
     * @return true if the client disconnected before the finish of a multiplayer game
     */
    private boolean knownClient(String nickname) {
        return clientsDisconnected.containsKey(nickname) || clientsDisconnectedGameFinished.containsKey(nickname);
    }

    public void removeGame(Controller controller){
        activeGames.remove(controller);
    }

    public void addClientHandler(ClientHandler clientHandler){
        lockLobby.lock();
        clientsInLobby.add(clientHandler);
        lockLobby.unlock();
    }

}