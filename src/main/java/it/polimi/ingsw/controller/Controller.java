package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.controller.game_phases.*;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import it.polimi.ingsw.jsonParsers.LightCardsParser;
import it.polimi.ingsw.messages.toClient.NotifyClientDisconnection;
import it.polimi.ingsw.messages.toClient.WelcomeBackMessage;
import it.polimi.ingsw.messages.toClient.matchData.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.persistency.GameHistory;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhase;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhaseSingle;
import it.polimi.ingsw.model.persistency.PersistentControllerSetUpPhase;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.controller.game_phases.SinglePlayerEndPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.messages.toClient.TextMessage;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.server.Server;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private int controllerID;
    private List<ClientHandler> clientHandlers;
    private ReentrantLock lockPlayers = new ReentrantLock(true);
    private ReentrantLock lockConnections = new ReentrantLock(true);
    private Server server;
    private final String RELOAD = "RELOAD";

    public Controller(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.game = new Game(gameMode);
        this.clientHandlers = new LinkedList<>();
    }

    /**
     * Method to handle a client's disconnection when a {@link Game} has already started
     * @param nickname the nickname of the disconnected client
     */
    public synchronized void handleClientDisconnection(String nickname){
        ClientHandler connection = getConnectionByNickname(nickname);

        if (connection.getGameMode() == GameMode.SINGLE_PLAYER){
            //SINGLE PLAYER
            clientHandlers.remove(connection);
            server.removeConnectionGameSinglePlayer(connection);

        } else {
            //MULTIPLAYER
            if (gamePhase instanceof EndPhase)
                server.removeConnectionGame(connection);
            else
                handleMultiplayerDisconnection(nickname);
        }
    }

    /**
     * Method to handle a client's disconnection when a {@link Game} has already started and the {@link GameMode} is multiplayer
     * @param nickname the nickname of the disconnected client
     */
    private void handleMultiplayerDisconnection(String nickname) {
        checkEndMultiplayerGame(nickname);
    }

    /**
     * Method to whether the game should be canceled
     * @param nickname the nickname of the disconnected client
     */
    private void checkEndMultiplayerGame(String nickname){
        getPlayerByNickname(nickname).setActive(false);
        if (getPlayers().stream().filter(Player::isActive).count() == 1 || (gamePhase instanceof SetUpPhase && getConnectionByNickname(nickname).getClientHandlerPhase() != ClientHandlerPhase.SET_UP_FINISHED)){
            clientHandlers.remove(getConnectionByNickname(nickname));
            forceEndMultiplayerGame();
        } else {
            server.removeConnectionGame(getConnectionByNickname(nickname));
            if (gamePhase instanceof SetUpPhase)
                handleMultiplayerDisconnectionSetUpPhase(nickname);

            if (gamePhase instanceof MultiplayerPlayPhase)
                handleMultiplayerDisconnectionGamePhase(nickname);
        }
    }

    /**
     * Method to force the end of a {@link Game}
     */
    private void forceEndMultiplayerGame(){
        for (Player player : getPlayers()) {
            //For each player that is still active I notify the end of the game and I reinsert him in the lobby room
            if (player.isActive()) {
                getConnectionByNickname(player.getNickname()).sendMessageToClient(new NotifyClientDisconnection(player.getNickname(), gamePhase instanceof SetUpPhase, true));
                getConnectionByNickname(player.getNickname()).setGameStarted(false);
                getConnectionByNickname(player.getNickname()).setController(null);
                getConnectionByNickname(player.getNickname()).setClientHandlerPhase(ClientHandlerPhase.WAITING_IN_THE_LOBBY);
                server.addClientHandler(getConnectionByNickname(player.getNickname()));
            } else {
                server.removeNickname(player.getNickname());
            }
        }
        GameHistory.removeOldGame(controllerID);
        server.removeGame(this);
        server.newGameManager();
    }

    /**
     * Method to handle a client's disconnection when a {@link Game} has already started, the {@link GameMode} is multiplayer and the game was still in the set up phase.
     * @param nickname the nickname of the disconnected client
     */
    private void handleMultiplayerDisconnectionSetUpPhase(String nickname){
        getClientHandlers().stream().filter(x -> !x.getNickname().equals(nickname)).collect(Collectors.toList()).forEach(x -> x.sendMessageToClient(new NotifyClientDisconnection(nickname, true, false)));
        server.removeConnectionGame(getConnectionByNickname(nickname));
        if (gamePhase instanceof SetUpPhase)
            ((SetUpPhase) gamePhase).endPhaseManagerDisconnection();
    }

    /**
     * Method to handle a client's disconnection when a {@link Game} has already started, the {@link GameMode} is multiplayer and the game was in the play phase.
     * @param nickname the nickname of the disconnected client
     */
    private void handleMultiplayerDisconnectionGamePhase(String nickname){
            getPlayerByNickname(nickname).setActive(false);
            sendMessageToAll(new NotifyClientDisconnection(nickname, false, false));
            if (((MultiplayerPlayPhase) gamePhase).getTurnController().getCurrentPlayer().getNickname().equals(nickname)) {
                //THE PLAYER DISCONNECTED WAS THE TURN'S OWNER -> I check if he has already done his standard action
                if (!((MultiplayerPlayPhase) gamePhase).getTurnController().isStandardActionDone()) {
                    //IF HE HAS NOT DONE THE STANDARD ACTION YET -> INVALID TURN! UNDO OF THE TURN
                    game = new Game(((MultiplayerPlayPhase) gamePhase).getLastTurnGameCopy());
                    getPlayerByNickname(nickname).setActive(false);
                    sendMatchData(game, true);
                }
                ((MultiplayerPlayPhase) gamePhase).nextTurn();
            }
    }


    /**
     * Method to start the controller. A new SetUp phase is created
     */
    public void start(){
        if (GameHistory.retrieveGameFromControllerId(controllerID) != null)
            reloadAnOldGame();
        else
            startANewGame();
    }

    /**
     * Method to start a new game, used when there is no game to load from the json file
     */
    private void startANewGame(){
        Server.SERVER_LOGGER.log(Level.INFO, "Creating a new " + game.getGameMode().name().replace("_", " ") + " game with id [" + controllerID + "], player" + (game.getGameMode()==GameMode.MULTI_PLAYER ?"s: " : ": ")  + clientHandlers.stream().map(ClientHandler::getNickname).collect(Collectors.toList()));
        this.setGamePhase(new SetUpPhase());
    }

    /**
     * Method to retrieve an old {@link Game} from the memory
     */
    private void reloadAnOldGame(){
        Server.SERVER_LOGGER.log(Level.INFO, "Retrieving an old " + game.getGameMode().name().replace("_", " ") + " game with id [" + controllerID + "], player" + (game.getGameMode()==GameMode.MULTI_PLAYER ?"s: " : ": ")  + clientHandlers.stream().map(ClientHandler::getNickname).collect(Collectors.toList()));
        if (GameHistory.isSetUpPhase(controllerID))
            reloadSetUpPhase();
        else
            reloadPlayPhase();
    }

    /**
     * Method to retrieve the {@link SetUpPhase} of an old {@link Game}
     */
    private void reloadSetUpPhase(){
        clientHandlers.forEach(x -> x.sendMessageToClient(new WelcomeBackMessage(x.getNickname(), false)));
        PersistentControllerSetUpPhase controller = GameHistory.retrieveSetUpController(controllerID);
        game = new Game(controller.getGame());
        gamePhase = new SetUpPhase(controller.getResourcesToStore(), this);
        ((SetUpPhase) gamePhase).reloadPhase();
    }

    /**
     * Method to retrieve the {@link PlayPhase} of an old {@link Game}
     */
    private void reloadPlayPhase(){
        clientHandlers.forEach(x -> x.sendMessageToClient(new WelcomeBackMessage(x.getNickname(), false)));
        if (game.getGameMode() == GameMode.MULTI_PLAYER)
            reloadMultiplayerPlayPhase();
        else
            reloadSinglePlayerPlayPhase();
    }

    /**
     * Method to retrieve the {@link MultiplayerPlayPhase} of an old {@link Game}
     */
    private void reloadMultiplayerPlayPhase(){
        PersistentControllerPlayPhase controller = GameHistory.retrievePlayController(controllerID);
        game = new Game(controller.getGame());
        getPlayers().forEach(x -> x.setActive(true));
        clientHandlers.forEach(x -> x.sendMessageToClient(new WelcomeBackMessage(x.getNickname(), false)));
        sendLightCards();
        sendMatchData(game, false);
        gamePhase = new MultiplayerPlayPhase(this, controller.getLastPlayer(), controller.isEndTriggered());
        ((PlayPhase)gamePhase).restartLastTurn();
    }

    /**
     * Method to retrieve the {@link SinglePlayerPlayPhase} of an old {@link Game}
     */
    private void reloadSinglePlayerPlayPhase(){
        PersistentControllerPlayPhaseSingle controller = GameHistory.retrievePlayControllerSingle(controllerID);
        game = new Game(controller.getGame());
        clientHandlers.get(0).sendMessageToClient(new WelcomeBackMessage(clientHandlers.get(0).getNickname(), false));
        sendLightCards();
        sendMatchData(game, false);
        gamePhase = new SinglePlayerPlayPhase(this, controller.getLastPlayer(), controller.isEndTriggered(), controller.getBlackCrossPosition(), controller.getTokens());
        if (((SinglePlayerPlayPhase) gamePhase).wasEndTriggered())
            endMatch();
        else
            ((PlayPhase)gamePhase).restartLastTurn();
    }


    /**
     * Method to add a connection to the client handlers' list
     * @param connection {@link ClientHandler} of the connection to add
     */
    public void addConnection(ClientHandler connection){
        lockConnections.lock();
        try {
            this.clientHandlers.add(connection);
        } finally {
            lockConnections.unlock();
        }
    }

    /**
     * Method to remove a connection from the client handlers' list. Used in case of disconnection of a client.
     * @param connection {@link ClientHandler} of the connection to remove
     */
    public void removeConnection(ClientHandler connection){
        lockConnections.lock();
        try {
            this.clientHandlers.remove(connection);
        } finally {
            lockConnections.unlock();
        }
    }

    public synchronized void handleMessage(MessageToServer message, ClientHandlerInterface clientHandler){
        gamePhase.handleMessage(message, (ClientHandler) clientHandler);
    }

    /**
     * Method to send all the Match Data to the client
     * @param game the {@link Game} the client is playing in
     * @param connection the {@link ClientHandler} of the client
     * @param disconnection true if I am sending the data because a client disconnected during his turn (I did the undo of his turn if he has not finished the standard action)
     */
    public void sendMatchData(Game game, ClientHandler connection, boolean disconnection){
        connection.sendMessageToClient(new ReloadMatchData(true, disconnection));
        connection.sendMessageToClient(new LoadDevelopmentCardGrid(connection.getNickname(), game.getDevelopmentCardGrid().getAvailableCards().stream().map(Card::getID).collect(Collectors.toList())));
        connection.sendMessageToClient(new UpdateMarketView(RELOAD, game.getMarket().getMarketTray(), game.getMarket().getSlideMarble()));
        for (Player gamePlayer : getPlayers()) {

                // 1. I create a map with the leader cards of the gamePlayer I am analyzing
                Map<Integer, Boolean> leaderCards = gamePlayer.getPersonalBoard().getLeaderCardsMap();
                connection.sendMessageToClient(new ReloadLeaderCardsOwned(gamePlayer.getNickname(), leaderCards));

                //2. Development cards
                connection.sendMessageToClient(new LoadDevelopmentCardSlots(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdSlots()));
                connection.sendMessageToClient(new ReloadDevelopmentCardsVictoryPoints(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getVictoryPointsDevelopmentCardSlots()));

                //3. Marker position
                connection.sendMessageToClient(new UpdateMarkerPosition(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getMarkerPosition()));

                //4. Depots status
                connection.sendMessageToClient(new UpdateDepotsStatus(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getStrongboxStatus(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getLeaderStatus()));

                //5. Pope tiles
                connection.sendMessageToClient(new ReloadPopesFavorTiles(gamePlayer.getNickname(), gamePlayer.getPersonalBoard().getPopesTileStates()));
        }
        connection.sendMessageToClient(new ReloadMatchData(false, disconnection));
    }

    /**
     * Method to send the Match Data to all the clients connected
     * @param game the game the clients are playing in
     * @param disconnection true if I am sending the data because a client disconnected during his turn (I did the undo of his turn if he has not finished the standard action)
     */
    public void sendMatchData(Game game, boolean disconnection){
        assert game!=null;
        lockConnections.lock();
        for (ClientHandler player : clientHandlers){
            sendMatchData(game, player, disconnection);
        }
        lockConnections.unlock();
    }

    public void sendLightCards() {
        getClientHandlers().forEach(x -> sendLightCards(x.getNickname()));
    }

    public void sendLightCards(String nickname){
        List<LightLeaderCard> leaderCards = LightCardsParser.getLightLeaderCards(LeaderCardParser.parseCards());
        List<LightDevelopmentCard> developmentCards = LightCardsParser.getLightDevelopmentCards(DevelopmentCardParser.parseCards());
        getConnectionByNickname(nickname).sendMessageToClient(new LoadDevelopmentCardsMessage(developmentCards));
        getConnectionByNickname(nickname).sendMessageToClient(new LoadLeaderCardsMessage(leaderCards));
    }

    /**
     * Method to send the same message to all the clients connected
     * @param message the {@link MessageToClient} to be sent
     */
    public void sendMessageToAll(MessageToClient message){
        lockConnections.lock();
        try{
            for (ClientHandler clientHandler : clientHandlers)
                clientHandler.sendMessageToClient(message);
        } finally {
            lockConnections.unlock();
        }
    }

    public void endMatch(){
        setGamePhase(gamePhase instanceof MultiplayerPlayPhase ? new MultiplayerEndPhase() : new SinglePlayerEndPhase());
    }

    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public Server getServer() {
        return server;
    }

    public int getControllerID() {
        return controllerID;
    }

    public Game getGame(){
        return this.game;
    }

    public List<String> getNicknames(){
        return clientHandlers.stream().map(ClientHandler::getNickname).collect(Collectors.toList());
    }

    public ClientHandler getConnectionByNickname(String nickname){
        lockConnections.lock();
        try{
            for (ClientHandler clientHandler : clientHandlers){
                if (clientHandler.getNickname().equals(nickname)){
                    return clientHandler;
                }
            }
        } finally {
            lockConnections.unlock();
        }
        return null;
    }

    public List<Player> getPlayers(){
        try {
            return game.getPlayers();
        } catch (InvalidMethodException | ZeroPlayerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Player getPlayerByNickname(String nickname){
        Player player = null;
        for (Player p : getPlayers()) {
            if (p.getNickname().equals(nickname))
                player = p;
        }
        return player;
    }

    public GamePhase getGamePhase(){
        return gamePhase;
    }

    public void setServer (Server server) {
        this.server = server;
    }


    public void setControllerID(int controllerID) {
        this.controllerID = controllerID;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        sendMessageToAll(new TextMessage(gamePhase.toString() + " has started!"));
        gamePhase.executePhase(this);
    }
}
