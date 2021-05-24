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

    public void handleClientDisconnection(String nickname){
        ClientHandler connection = getConnectionByNickname(nickname);
        //SINGLE PLAYER
        if (connection.getGameMode() == GameMode.SINGLE_PLAYER){
            connection.getServer().gameEnded(this);
            return;
        }


        //DISCONNECTION DURING SETUP PHASE
        if (gamePhase instanceof SetUpPhase){

            if (connection.getClientHandlerPhase() == ClientHandlerPhase.SET_UP_FINISHED){
                //CASE 1: THE CLIENT HAS ALREADY FINISHED THE SETUP PHASE: the game starts and the player is set as inactive
                getClientHandlers().forEach(x -> x.sendMessageToClient(new NotifyClientDisconnection(nickname, true, false)));
                server.removeConnectionGame(connection, false);
                getPlayerByNickname(nickname).setActive(false);
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
        }

        if (gamePhase instanceof MultiplayerPlayPhase){
            if (!server.removeConnectionGame(connection, false))
                return;

            getPlayerByNickname(nickname).setActive(false);
            sendMessageToAll(new NotifyClientDisconnection(nickname, false, false));

            //THE PLAYER DISCONNECTED WAS THE TURN'S OWNER -> I check if he has already done his standard action
            if (((MultiplayerPlayPhase) gamePhase).getTurnController().getCurrentPlayer().getNickname().equals(nickname)){
                if (!((MultiplayerPlayPhase) gamePhase).getTurnController().isStandardActionDone()){
                    //IF HE HAS NOT DONE THE STANDARD ACTION YET -> INVALID TURN! UNDO OF THE TURN
                    game = new Game(((MultiplayerPlayPhase) gamePhase).getLastTurnGameCopy());
                    game.getPlayerByNickname(nickname).setActive(false);
                    sendMatchData(game, true);
                }
                ((MultiplayerPlayPhase) gamePhase).nextTurn();
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

        //SETUP MESSAGES
        if (gamePhase instanceof SetUpPhase) {
            ((SetUpPhase) gamePhase).handleMessage(message, (ClientHandler) clientHandler);
        }

        //IN-GAME MESSAGES
        if(gamePhase instanceof PlayPhase){
            ((PlayPhase) gamePhase).handleMessage(message,(ClientHandler) clientHandler);
        }
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

    public void sendMatchData(Game game, boolean disconnection){
        assert game!=null;
        //Inform all the clients that a previous game status is being restored
        sendMessageToAll(new ReloadMatchData(true, disconnection));
        if (!disconnection){
            //Resend all the cards
        }
        sendMessageToAll(new LoadDevelopmentCardGrid(game.getDevelopmentCardGrid().getAvailableCards().stream().map(Card::getID).collect(Collectors.toList())));
        sendMessageToAll(new UpdateMarketView(RELOAD, game.getMarket().getMarketTray(), game.getMarket().getSlideMarble()));
        lockConnections.lock();
        for (ClientHandler player : clientHandlers){
                for (Player gamePlayer : getPlayers()) {

                    // 1. I create a map with the leader cards of the gamePlayer I am analyzing
                    Map<Integer, Boolean> leaderCards = gamePlayer.getPersonalBoard().getLeaderCardsMap();
                    player.sendMessageToClient(new ReloadLeaderCardsOwned(gamePlayer.getNickname(), leaderCards));

                    //2. Development cards
                    //TODO remove next row when raffa has finished
                    player.sendMessageToClient(new ReloadDevelopmentCardOwned(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getHiddenDevelopmentCardColours(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdFirstRow()));
                    player.sendMessageToClient(new LoadDevelopmentCardSlots(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdSlots()));
                    player.sendMessageToClient(new ReloadDevelopmentCardsVictoryPoints(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getVictoryPointsDevelopmentCardSlots()));

                    //3. Marker position
                    player.sendMessageToClient(new UpdateMarkerPosition(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getMarkerPosition()));

                    //4. Depots status
                    player.sendMessageToClient(new UpdateDepotsStatus(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getStrongboxStatus(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getLeaderStatus()));

                    //5. Pope tiles
                    player.sendMessageToClient(new ReloadPopesFavorTiles(gamePlayer.getNickname(), gamePlayer.getPersonalBoard().getPopesTileStates()));
                }

        }
        lockConnections.unlock();
        sendMessageToAll(new ReloadMatchData(false, disconnection));
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
        List<LightLeaderCard> leaderCards = LightCardsParser.getLightLeaderCards(LeaderCardParser.parseCards());
        List<LightDevelopmentCard> developmentCards = LightCardsParser.getLightDevelopmentCards(DevelopmentCardParser.parseCards());
        for (String nickname : getNicknames()) {
            ClientHandler connection = getConnectionByNickname(nickname);
            connection.sendMessageToClient(new LoadDevelopmentCardsMessage(developmentCards));
            connection.sendMessageToClient(new LoadLeaderCardsMessage(leaderCards));
        }
    }
}
