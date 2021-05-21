package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.game_phases.*;
import it.polimi.ingsw.messages.toClient.matchData.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.LeaderCard;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private List<ClientHandler> clientHandlers;
    private ReentrantLock lockPlayers = new ReentrantLock(true);
    private ReentrantLock lockConnections = new ReentrantLock(true);
    private final String RELOAD = "RELOAD";

    public Controller(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.game = new Game(gameMode);
        this.clientHandlers = new LinkedList<>();
    }

    /**
     * Method to start the controller. A new SetUp phase is created
     */
    public void start(){
        this.setGamePhase(new SetUpPhase());
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

    public void sendMatchData(Game game, boolean disconnection){
        assert game!=null;
        //Inform all the clients that a previous game status is being restored
        sendMessageToAll(new ReloadMatchData(true, disconnection));
        if (!disconnection){
            //Resend all the cards
        }
        sendMessageToAll(new LoadDevelopmentCardGrid(game.getDevelopmentCardGrid().getAvailableCards().stream().map(Card::getID).collect(Collectors.toList())));
        sendMessageToAll(new UpdateMarketView(RELOAD, game.getMarket().getMarketTray(), game.getMarket().getSlideMarble()));
        for (Player player : getPlayers()){
            if (player.isActive()) {
                for (Player gamePlayer : getPlayers()) {

                    // 1. I create a map with the leader cards of the gamePlayer I am analyzing
                    Map<Integer, Boolean> leaderCards = new HashMap<>();
                    for (LeaderCard card : gamePlayer.getPersonalBoard().getLeaderCards()){
                        if (card.isActive())
                            leaderCards.put (card.getID(), true);
                        else{
                            if (gamePlayer.getNickname().equals(player.getNickname()))
                                leaderCards.put(card.getID(), false);
                        }
                    }
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new ReloadLeaderCardsOwned(gamePlayer.getNickname(), leaderCards));

                    //2. Development cards
                    //TODO remove next row when raffa has finished
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new ReloadDevelopmentCardOwned(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getHiddenDevelopmentCardColours(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdFirstRow()));
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new LoadDevelopmentCardSlots(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdSlots()));
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new ReloadDevelopmentCardsVictoryPoints(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getVictoryPointsDevelopmentCardSlots()));

                    //3. Marker position
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new UpdateMarkerPosition(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getMarkerPosition()));

                    //4. Depots status
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new UpdateDepotsStatus(gamePlayer.getNickname(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getStrongboxStatus(), game.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getLeaderStatus()));

                    //5. Pope tiles
                    getConnectionByNickname(player.getNickname()).sendMessageToClient(new ReloadPopesFavorTiles(gamePlayer.getNickname(), gamePlayer.getPersonalBoard().getPopesTileStates()));
                }
            }
        }
        sendMessageToAll(new ReloadMatchData(false, disconnection));
    }


    public void endMatch(){
        setGamePhase(gamePhase instanceof MultiplayerPlayPhase ? new MultiplayerEndPhase() : new SinglePlayerEndPhase());
    }
}
