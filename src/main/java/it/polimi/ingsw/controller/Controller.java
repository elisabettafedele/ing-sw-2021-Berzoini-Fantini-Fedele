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
    private List<Player> players;
    private List<ClientHandler> clientHandlers;
    private ReentrantLock lockPlayers = new ReentrantLock(true);
    private ReentrantLock lockConnections = new ReentrantLock(true);
    private GameMessageManager gameMessageManager;
    public Controller(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.game = new Game(gameMode);
        this.clientHandlers = new LinkedList<>();
        //TODO this.gamePhase = new GamePhase() link the setup phase

    }

    public void start(){
        this.gamePhase = new SetUpPhase();
        this.gamePhase.executePhase(this);
    }

    public Game getGame(){
        return this.game;
    }

    public GamePhase getGamePhase(){
        return this.gamePhase;
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
        System.out.println("Getting the player for "+ nickname);
        for (Player p : getPlayers()) {
            if (p.getNickname().equals(nickname))
                player = p;
        }
        System.out.println("Got the player for "+ nickname);

        return player;
    }

    public GameMessageManager getGameMessageManager() {
        return gameMessageManager;
    }

    public void addConnection(ClientHandler connection){
        lockConnections.lock();
        try {
            this.clientHandlers.add(connection);
        } finally {
            lockConnections.unlock();
        }
    }

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
        if (message instanceof ChooseLeaderCardsResponse) {
            System.out.println("starting to handle the choose leader card resp of " + nickname);
            handleChooseLeaderCardResponse((ChooseLeaderCardsResponse) message, clientHandler);
            System.out.println("finished to handle the choose leader card resp of " + nickname);

        }

        if (message instanceof ChooseResourceAndStorageTypeResponse) {
            System.out.println("starting to handle the choose resource  resp of " + nickname);
            handleChooseResourceAndStorageTypeRequest((ChooseResourceAndStorageTypeResponse) message, clientHandler);
            System.out.println("starting to handle the choose resource resp of " + nickname);

        }
    }

    public void  handleChooseLeaderCardResponse(ChooseLeaderCardsResponse message, ClientHandlerInterface clientHandler){
        String nickname = clientHandler.getNickname();
        Player player = getPlayerByNickname(nickname);
        System.out.println("I am " + nickname + "got the player!");
        assert (gamePhase instanceof SetUpPhase);
        System.out.println("I am " + nickname + "got the player post assertion!");

        List <Integer> discardedCards = message.getDiscardedLeaderCards();
        System.out.println("I am " + nickname + "got the discarded cards!" + discardedCards +"CARDS PRE FOR");

        for (Integer id : discardedCards) {
            System.out.println("I am " + nickname + "removed " + id);
            player.getPersonalBoard().removeLeaderCard(id);
            System.out.println("I am " + nickname + "removed " + id);

        }
        System.out.println("I am " + nickname + "removed cards");
        if (((SetUpPhase) gamePhase).getNumberOfInitialResourcesByNickname(nickname) == 0){
            //TODO send a message with his personal board view
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_HIS_TURN);
            clientHandler.sendMessageToClient(new WaitingInTheLobbyMessage());
            return;
        }

        //Available resource types
        List<String> resourceTypes = Stream.of(Resource.values()).map(Enum::name).collect(Collectors.toList());

        //Available depots
        List<String> storageTypes = new ArrayList<>();
        storageTypes.add(ResourceStorageType.WAREHOUSE_FIRST_DEPOT.name());
        storageTypes.add(ResourceStorageType.WAREHOUSE_SECOND_DEPOT.name());
        storageTypes.add(ResourceStorageType.WAREHOUSE_THIRD_DEPOT.name());
        System.out.println("I am " + nickname + ": depot");

        //Number of resources to choose and store
        int quantity = ((SetUpPhase) gamePhase).getNumberOfInitialResourcesByNickname(nickname);

        //I ask to the player to choose one or two type of resource
        System.out.println("Sending message to "+ nickname);

        clientHandler.sendMessageToClient(new ChooseResourceAndStorageTypeRequest(resourceTypes, storageTypes, quantity));
        System.out.println("Message sent to "+ nickname);
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE);
    }

    private void handleChooseResourceAndStorageTypeRequest(ChooseResourceAndStorageTypeResponse message, ClientHandlerInterface clientHandler){
        Player player = null;
        lockPlayers.lock();
        try {
            player = getPlayerByNickname(clientHandler.getNickname());
        } finally {
            lockConnections.unlock();
        }
        Map<String, String> storage = message.getStorage();
        for (String resource : storage.keySet()) {
            try {
                player.getPersonalBoard().addResources(ResourceStorageType.valueOf(storage.get(resource)), Resource.valueOf(resource), 1);
            } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
                e.printStackTrace();
            }
        }
    }


}
