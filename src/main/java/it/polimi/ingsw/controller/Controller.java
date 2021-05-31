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

    public void setServer (Server server){
        this.server = server;
    }

    public synchronized void handleClientDisconnection(String nickname){
        ClientHandler connection = getConnectionByNickname(nickname);
        //SINGLE PLAYER
        if (connection.getGameMode() == GameMode.SINGLE_PLAYER){
            connection.getServer().gameEnded(this);
            return;
        }

        //MULTIPLAYER
        //DISCONNECTION DURING SETUP PHASE
        if (gamePhase instanceof SetUpPhase){

            if (connection.getClientHandlerPhase() == ClientHandlerPhase.SET_UP_FINISHED){
                //CASE 1: THE CLIENT HAS ALREADY FINISHED THE SETUP PHASE: the game starts and the player is set as inactive
                if (!server.removeConnectionGame(connection, false))
                    return;
                //If I am here is because the game still exists
                getClientHandlers().stream().filter(x -> !x.getNickname().equals(nickname)).collect(Collectors.toList()).forEach(x -> x.sendMessageToClient(new NotifyClientDisconnection(nickname, true, false)));
                getPlayerByNickname(nickname).setActive(false);
                if (gamePhase instanceof SetUpPhase)
                    ((SetUpPhase) gamePhase).endPhaseManager(connection);
            } else {
                //CASE 2: THE CLIENT HAS NOT FINISHED THE SETUP PHASE YET
                connection.getServer().removeConnectionGame(connection, true);
                lockConnections.lock();
                for (ClientHandler other : getClientHandlers()) {
                    server.removeConnectionGame(other, true);
                    if (other.isGameStarted() && other.isActive()) {
                        other.sendMessageToClient(new NotifyClientDisconnection(nickname, true, true));
                        other.setGameStarted(false);
                        other.setClientHandlerPhase(ClientHandlerPhase.WAITING_NICKNAME);
                        server.handleNicknameChoice(other);
                    }
                }
                lockConnections.unlock();
                connection.setController(null);
            }
            return;
        }

        if (gamePhase instanceof MultiplayerPlayPhase){

            if (clientHandlers.size() < 1){
                //The game is cancelled
                server.removeConnectionGame(connection, false);
            } else {
                //The game will still be played
                getPlayerByNickname(nickname).setActive(false);
                server.removeConnectionGame(connection, false);
                sendMessageToAll(new NotifyClientDisconnection(nickname, false, false));
                if (((MultiplayerPlayPhase) gamePhase).getTurnController().getCurrentPlayer().getNickname().equals(nickname)){
                    //THE PLAYER DISCONNECTED WAS THE TURN'S OWNER -> I check if he has already done his standard action
                    if (!((MultiplayerPlayPhase) gamePhase).getTurnController().isStandardActionDone()){
                        //IF HE HAS NOT DONE THE STANDARD ACTION YET -> INVALID TURN! UNDO OF THE TURN
                        game = new Game(((MultiplayerPlayPhase) gamePhase).getLastTurnGameCopy());
                        sendMatchData(game, true);
                    }
                    ((MultiplayerPlayPhase) gamePhase).nextTurn();
                }
            }





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

    private void startANewGame(){
        this.setGamePhase(new SetUpPhase());
    }

    private void reloadAnOldGame(){
        if (GameHistory.isSetUpPhase(controllerID))
            reloadSetUpPhase();
        else
            reloadPlayPhase();
    }

    private void reloadSetUpPhase(){
        clientHandlers.forEach(x -> x.sendMessageToClient(new WelcomeBackMessage(x.getNickname(), false)));
        PersistentControllerSetUpPhase controller = GameHistory.retrieveSetUpController(controllerID);
        game = new Game(controller.getGame());
        gamePhase = new SetUpPhase(controller.getResourcesToStore(), this);
        ((SetUpPhase) gamePhase).reloadPhase();
    }

    private void reloadPlayPhase(){
        if (game.getGameMode() == GameMode.MULTI_PLAYER)
            reloadMultiplayerPlayPhase();
        else
            reloadSinglePlayerPlayPhase();
    }

    private void reloadMultiplayerPlayPhase(){
        PersistentControllerPlayPhase controller = GameHistory.retrievePlayController(controllerID);
        game = new Game(controller.getGame());
        getPlayers().forEach(x -> x.setActive(true));
        sendLightCards();
        clientHandlers.forEach(x -> x.sendMessageToClient(new WelcomeBackMessage(x.getNickname(), false)));
        sendMatchData(game, false);
        gamePhase = new MultiplayerPlayPhase(this, controller.getLastPlayer(), controller.isEndTriggered());
        ((PlayPhase)gamePhase).restartLastTurn();
    }

    private void reloadSinglePlayerPlayPhase(){
        PersistentControllerPlayPhaseSingle controller = GameHistory.retrievePlayControllerSingle(controllerID);
        game = new Game(controller.getGame());
        sendLightCards();
        clientHandlers.forEach(x -> x.sendMessageToClient(new WelcomeBackMessage(x.getNickname(), false)));
        sendMatchData(game, false);
        gamePhase = new SinglePlayerPlayPhase(this, controller.getLastPlayer(), controller.isEndTriggered(), controller.getBlackCrossPosition(), controller.getTokens());
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

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        sendMessageToAll(new TextMessage(gamePhase.toString() + " has started!"));
        gamePhase.executePhase(this);
    }

    public GamePhase getGamePhase(){
        return gamePhase;
    }

    public void sendMessageToAll(MessageToClient message){
        lockConnections.lock();
        try{
            for (ClientHandler clientHandler : clientHandlers)
                clientHandler.sendMessageToClient(message);
        } finally {
            lockConnections.unlock();
        }
    }

    public void sendMessageToAllExcept(MessageToClient message, String nickname){
        lockConnections.lock();
        try{
            for (ClientHandler clientHandler : clientHandlers) {
                if (!clientHandler.getNickname().equals(nickname))
                    clientHandler.sendMessageToClient(message);
            }
        } finally {
            lockConnections.unlock();
        }
    }

    public void sendMatchData(Game game, ClientHandler connection, boolean disconnection){
        connection.sendMessageToClient(new ReloadMatchData(true, disconnection));
        connection.sendMessageToClient(new LoadDevelopmentCardGrid(connection.getNickname(), game.getDevelopmentCardGrid().getAvailableCards().stream().map(Card::getID).collect(Collectors.toList())));
        connection.sendMessageToClient(new UpdateMarketView(RELOAD, game.getMarket().getMarketTray(), game.getMarket().getSlideMarble()));
        for (Player gamePlayer : getPlayers()) {

                // 1. I create a map with the leader cards of the gamePlayer I am analyzing
                Map<Integer, Boolean> leaderCards = gamePlayer.getPersonalBoard().getLeaderCardsMap();
                connection.sendMessageToClient(new ReloadLeaderCardsOwned(gamePlayer.getNickname(), leaderCards));

                //2. Development cards
                //TODO remove next row when raffa has finished
                connection.sendMessageToClient(new ReloadDevelopmentCardOwned(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getHiddenDevelopmentCardColours(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdFirstRow()));
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

    public void sendMatchData(Game game, boolean disconnection){
        assert game!=null;
        lockConnections.lock();
        for (ClientHandler player : clientHandlers){
            sendMatchData(game, player, disconnection);
        }
        lockConnections.unlock();
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

    public void setControllerID(int controllerID) {
        this.controllerID = controllerID;
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
}
