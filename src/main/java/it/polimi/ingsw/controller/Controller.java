package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.ChooseResourceAndStorageTypeRequest;
import it.polimi.ingsw.messages.toClient.MessageToClient;
import it.polimi.ingsw.messages.toClient.TextMessage;
import it.polimi.ingsw.messages.toClient.WaitingInTheLobbyMessage;
import it.polimi.ingsw.messages.toServer.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.ChooseResourceAndStorageTypeResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private List<ClientHandler> clientHandlers;
    private ReentrantLock lockPlayers = new ReentrantLock(true);
    private ReentrantLock lockConnections = new ReentrantLock(true);
    public Controller(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.game = new Game(gameMode);
        this.clientHandlers = new LinkedList<>();
        //TODO this.gamePhase = new GamePhase() link the setup phase

    }

    /**
     * Method to start the controller. A new SetUp phase is created
     */
    public void start(){
        this.setGamePhase(new SetUpPhase());
        this.gamePhase.executePhase(this);
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
        String nickname = clientHandler.getNickname();

        //SETUP MESSAGES
        if (gamePhase instanceof SetUpPhase) {
            ((SetUpPhase) gamePhase).handleMessage(message, (ClientHandler) clientHandler);
        }

        //IN-GAME MESSAGES
    }

    public Game getGame(){
        return this.game;
    }

    public List<String> getNicknames(){
        return clientHandlers.stream().map(x -> x.getNickname()).collect(Collectors.toList());
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
}
